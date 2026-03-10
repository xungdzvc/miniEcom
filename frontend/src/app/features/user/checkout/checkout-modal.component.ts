import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../../shared/data-access/order.service';
import { NotificationService } from '../../../core/services/notification.service';
import { DirectCheckoutRequest } from '../../../shared/models/core/checkout/direct-checkout.model';
import { CouponService } from '../../../shared/data-access/coupon.service';
import { Router } from '@angular/router';
import { OrderCheckoutResponse } from '../../../shared/models/core/order/order-response.model';
import { AuthService } from '../../../core/services/auth.service';
import { environment } from '../../../../../enviroments/environment';@Component({
  selector: 'app-checkout-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout-modal.component.html',
  styleUrls: ['./checkout-modal.component.css']
})
export class CheckoutComponent implements OnInit, OnDestroy {
  // --- INPUTS ---
  @Input() mode: 'PRODUCT' | 'DEPOSIT' = 'PRODUCT'; // Chế độ: Mua hàng hoặc Nạp tiền
  @Input() product: any; // Dùng cho mode PRODUCT
  @Input() depositAmount: number = 0; // Dùng cho mode DEPOSIT
  
  @Input() user: any;
  @Output() close = new EventEmitter<void>();

  // --- STATE ---
  step: 'CHECKOUT' | 'WAITING_PAYMENT' | 'SUCCESS' = 'CHECKOUT';
  paymentMethod: 'WALLET' | 'ORDER_BANKING' = 'WALLET';
  
  isLoading = false;
  fileBaseUrl = environment.fileBaseUrl;

  couponCode = '';
  discountAmount = 0;
  orderData: OrderCheckoutResponse | null = null;

  // QR & Polling Logic
  qrData: any = null;
  private pollInterval: any;
  timeLeft: string = '';
  private countdownInterval: any;
  isExpired: boolean = false;

  constructor(
    private orderService: OrderService,
    private notify: NotificationService,
    private couponService: CouponService,
    private router: Router,
    private auth: AuthService
  ) {}

  ngOnInit() {
    // Nếu là nạp tiền -> Bắt buộc dùng BANKING
    if (this.mode === 'DEPOSIT') {
      this.paymentMethod = 'ORDER_BANKING';
    } else {
      // Logic cũ cho mua hàng
      if (this.canPayByWallet) {
        this.paymentMethod = 'WALLET';
      } else {
        this.paymentMethod = 'ORDER_BANKING';
      }
    }
  }

  ngOnDestroy() {
    this.stopPolling();
  }

  // --- GETTERS (Giữ nguyên) ---
  get originalPrice(): number { return this.product?.price || 0; }
  get productDiscountPercent(): number { return this.product?.discount || 0; }
  get priceAfterProductDiscount(): number {
    if (this.productDiscountPercent > 0) return this.originalPrice * (1 - this.productDiscountPercent / 100);
    return this.originalPrice;
  }
  get savedByProduct(): number { return this.originalPrice - this.priceAfterProductDiscount; }
  get finalPrice(): number { const p = this.priceAfterProductDiscount - this.discountAmount; return p > 0 ? p : 0; }
  get canPayByWallet(): boolean { return (this.user?.vnd || 0) >= this.finalPrice; }

  getFileUrl(path?: string): string {
    if (!path) return 'https://placehold.co/1200x600';
    if (path.startsWith('http')) return path;
    return `${this.fileBaseUrl.replace(/\/$/, '')}/${path.replace(/^\//, '')}`;
  }

  // --- ACTIONS ---

  applyCoupon() {
    if (!this.couponCode) return;
    const code = this.couponCode.trim();
    this.isLoading = true;
    this.couponService.getCouponCodeByCode(code).subscribe({
      next: (res: any) => {
        const pct = Number(res?.data ?? 0) || 0;
        if (pct > 0) {
          this.discountAmount = (this.priceAfterProductDiscount * pct) / 100;
          this.notify.success(`Áp dụng mã giảm giá thành công (-${pct}%)`);
        } else {
          this.clearCoupon();
          this.notify.error('Mã giảm giá không hợp lệ.');
        }
        this.isLoading = false;
      },
      error: (err) => {
        this.clearCoupon();
        this.notify.error(err?.error?.message ?? 'Lỗi coupon.');
        this.isLoading = false;
      },
    });
  }

  clearCoupon(): void {
    this.couponCode = '';
    this.discountAmount = 0;
  }

  onPay() {
    if (this.paymentMethod === 'WALLET' && !this.canPayByWallet) {
      this.notify.error('Số dư ví không đủ. Vui lòng nạp thêm!');
      return;
    }

    this.isLoading = true;
    
    const request: DirectCheckoutRequest = {
      productId: this.product.id,
      quantity: 1,
      couponCode: this.couponCode,
      paymentMethod: this.paymentMethod
    };

    this.orderService.buyNowProduct(request).subscribe({
      next: (res: any) => {
        this.isLoading = false;
        this.orderData = res.data;
        this.auth.refreshUser().subscribe();
        if (res.success && this.orderData) {
          
          // CASE 1: SUCCESS
          if (this.orderData.status === 'SUCCESS' || this.orderData.status === 'PAID') {
            this.handleSuccess();
          } 
          
          // CASE 2: PENDING (QR)
          else if (this.orderData.status === 'PENDING' && this.orderData.qrcodeUrl) {
            this.step = 'WAITING_PAYMENT';
            
            // 👇 LOGIC MỚI: Lấy năm hiện tại động
            const currentYear = new Date().getFullYear(); 
            
            this.qrData = {
              qrUrl: this.orderData.qrcodeUrl,
              amount: this.orderData.total,
              // Kết quả: HD2026 + OrderId (Nếu năm nay là 2026)
              paymentContent: `HD${currentYear}${this.orderData.orderId}`
            };

            this.startPolling(this.orderData.orderId);

            if (this.orderData.expiresAt) {
              this.startCountdown(this.orderData.expiresAt);
            }
          }
        } else {
          this.notify.error(res.message || 'Giao dịch thất bại');
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.notify.error(err.error?.message || 'Lỗi hệ thống');
      }
    });
  }

  // --- POLLING & TIMER ---

  startPolling(orderId: number) {
    this.stopPolling();
    this.pollInterval = setInterval(() => {
      this.orderService.getPaymentStatus(orderId).subscribe({
        next: (res: any) => {
          // Xử lý linh hoạt format data trả về
          let status = '';

          if (res.data && typeof res.data === 'string') status = res.data;
          else if (res.data && res.data.status) status = res.data.status;
          else status = res.status;

          if (status === 'PAID' || status === 'SUCCESS') {
            this.handleSuccess();
          }
        },
        error: () => {} 
      });
    }, 3000);
  }

  startCountdown(expiresAtStr: string) {
    const expireTime = new Date(expiresAtStr).getTime();
    this.countdownInterval = setInterval(() => {
      const now = new Date().getTime();
      const distance = expireTime - now;

      if (distance < 0) {
        this.timeLeft = "00:00";
        this.isExpired = true;
        this.stopPolling();
      } else {
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);
        this.timeLeft = `${minutes < 10 ? '0' + minutes : minutes}:${seconds < 10 ? '0' + seconds : seconds}`;
      }
    }, 1000);
  }

  stopPolling() {
    if (this.pollInterval) clearInterval(this.pollInterval);
    if (this.countdownInterval) clearInterval(this.countdownInterval);
  }

  handleSuccess() {

    this.stopPolling();
    this.step = 'SUCCESS';
    this.notify.success('Giao dịch hoàn tất!');
  }

  closeModal() {
    this.stopPolling();
    this.close.emit();
  }
  
  copyContent(text: string) {
    if(text) {
      navigator.clipboard.writeText(text);
      this.notify.success('Đã sao chép nội dung CK');
    }
  }
  goToOrderDetail(orderId: number | null): void {
    if (!orderId) return;
    this.router.navigate([`/order/${orderId}/detail`]);
  }
}