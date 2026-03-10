import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { environment } from '../../../../../enviroments/environment';import { CartResponse } from '../../../shared/models/core/cart/cart.model';
import { Order } from '../../../shared/models/core/checkout/order.model';

import { CartService } from '../../../shared/data-access/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { ConfirmService } from '../../../shared/services/confirm.service';
import { ProductService } from '../../../shared/data-access/products/product.service';
import { CouponService } from '../../../shared/data-access/coupon.service';
import { OrderService } from '../../../shared/data-access/order.service';
import { PaymentViewModel } from '../../../shared/models/core/payment/payment-view.model';

type AppliedCoupon =
  | { type: 'percent'; value: number }
  | { type: 'amount'; value: number };

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
  fileBaseUrl = environment.fileBaseUrl;

  coupon = '';
  note = '';
  isPaying: boolean = false;
  cartItems: CartResponse | null = null;
  private appliedCoupon: AppliedCoupon | null = null;

  userBalance = 0;

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private notifi: NotificationService,
    private confirm: ConfirmService,
    private router: Router,
    private productService: ProductService,
    private couponService: CouponService,
    private orderService: OrderService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUserBalance();
    this.loadCart();
  }

  private loadUserBalance(): void {
    const user = this.authService.getCurrentUser?.() ?? this.authService.getCurrentUser?.();
    this.userBalance = Number(user?.vnd ?? 0) || 0;
  }

  private loadCart(): void {
    const user = this.authService.getCurrentUser?.();
    if (!user) return;

    this.cartService.getCart().subscribe({
      next: (res) => (this.cartItems = res.data ?? null),
      error: (err) => this.notifi.error(err?.message ?? 'Không tải được giỏ hàng'),
    });
  }

  

  // ===== coupon =====
  applyCoupon(): void {
    const code = this.coupon.trim();
    if (!code) {
      this.appliedCoupon = null;
      return;
    }

    this.couponService.getCouponCodeByCode(code).subscribe({
      next: (res: any) => {
        const pct = Number(res?.data ?? 0) || 0;
        if (pct > 0) {
          this.appliedCoupon = { type: 'percent', value: Math.min(100, Math.max(0, pct)) };
          this.notifi.success('Áp dụng mã giảm giá thành công!');
        } else {
          this.clearCoupon();
          this.notifi.error('Mã giảm giá không hợp lệ.');
        }
      },
      error: (err) => {
        this.clearCoupon();
        this.notifi.error(err?.error?.message ?? 'Mã giảm giá không hợp lệ.');
      },
    });
  }

  clearCoupon(): void {
    this.appliedCoupon = null;
    this.coupon = '';
  }

  // ===== qty =====
  incQty(it: any): void {
    this.updateQty(it, this.itemQty(it) + 1);
  }

  decQty(it: any): void {
    this.updateQty(it, Math.max(1, this.itemQty(it) - 1));
  }

  setQty(it: any, value: any): void {
    const n = Number(value);
    const next = Number.isFinite(n) ? Math.max(1, Math.floor(n)) : this.itemQty(it);
    this.updateQty(it, next);
  }
  viewDetail(slug: string): void {
    this.productService.updateViewProductBySlug(slug).subscribe({ error: () => {} });
    this.router.navigate(['/products', slug]);
  }


  // Trong component class
 // Biến điều khiển hiển thị overlay

  checkout(method: string): void {
    const cartId = this.cartItems?.id ?? 0;
    if (!cartId) {
      this.notifi.error('Giỏ hàng trống. Không thể thanh toán.');
      return;
    }

    // 1. Hiển thị Overlay
    this.isPaying = true;

    const orderRequest: Order = {
      cartId,
      couponCode: this.coupon ? this.coupon.trim() : '',
      paymentMethod: method,
    };

    const start = Date.now();
    
    const minMs = method === 'WALLET' ? 500 : 2000;

    this.orderService.pay(orderRequest).subscribe({
      next: (res: any) => {
        const checkout = res?.data;
        const elapsedTime = Date.now() - start;
        const wait = Math.max(0, minMs - elapsedTime);

        setTimeout(() => {
          this.isPaying = false;

          if (method === 'WALLET') {
              this.auth.refreshUser().subscribe();
              this.notifi.success('Thanh toán thành công!');
              this.router.navigate(['/orders']); 
          } else {
              if (checkout?.orderId) {
                const paymentView: PaymentViewModel = {
                  type: 'ORDER',
                  title: 'Thanh toán đơn hàng',
                  amount: checkout.total,
                  expiresAt: checkout.expiresAt,
                  qrCodeUrl: checkout.qrcodeUrl,
                  orderId: checkout.orderId
                };
                this.router.navigate(['/payment'], { state: { paymentView } });
              } else {
                  this.router.navigate(['/orders']);
              }
          }
          
        }, wait);
      },
      error: (err) => {
        const elapsedTime = Date.now() - start;
        const wait = Math.max(0, minMs - elapsedTime);

        setTimeout(() => {
          this.isPaying = false;
          this.notifi.error(err?.error?.message ?? err?.message ?? 'Thanh toán thất bại');
        }, wait);
      },
    });
  }


  topUp(): void {
    // this.router.navigate(['/wallet/topup']);
  }

  async remove(id: number, name: string): Promise<void> {
    if (!this.cartItems) return;

    const ok = await this.confirm.confirm(`Xóa "${name}" khỏi giỏ hàng?`, 'Xác nhận');
    if (!ok) return;
    this.cartService.removeProductFromCart(id).subscribe();
    this.cartItems = {
      ...this.cartItems,
      cartItems: this.cartItems.cartItems.filter((it) => it.id !== id),
    };

    
  }

  async clear(): Promise<void> {
    if (!this.cartItems) return;

    const ok = await this.confirm.confirm(`Xóa toàn bộ sản phẩm khỏi giỏ hàng?`, 'Xác nhận');
    if (!ok) return;
    this.cartItems = { ...this.cartItems, cartItems: [] };

    this.cartService.clearCart().subscribe();
    this.clearCoupon();
  }

  private updateQty(it: any, qty: number): void {
    if (!this.cartItems?.cartItems) return;

    this.cartItems = {
      ...this.cartItems,
      cartItems: this.cartItems.cartItems.map((x) =>
        x.id === it.id ? { ...x, quantity: qty } : x
      ),
    };

    this.cartService.updateQty(it.id, qty).subscribe(

    );
  }

  // ===== actions =====
  
  // ===== item helpers =====
  itemQty(it: any): number {
    return Math.max(1, Number(it?.quantity ?? 1) || 1);
  }

  itemOldPrice(it: any): number {
    return Number(it?.price ?? 0) || 0;
  }

  itemDiscountPercent(it: any): number {
    const pct = Number(it?.discount ?? 0) || 0;
    return Math.min(100, Math.max(0, pct));
  }

  itemCurrentPrice(it: any): number {
    const price = this.itemOldPrice(it);
    const pct = this.itemDiscountPercent(it);
    return Math.round(price * (1 - pct / 100));
  }

  itemLineTotal(it: any): number {
    return this.itemCurrentPrice(it) * this.itemQty(it);
  }

  private itemLineOldTotal(it: any): number {
    return this.itemOldPrice(it) * this.itemQty(it);
  }

  // ===== totals =====
  get subtotalOld(): number {
    return this.cartItems?.cartItems?.reduce((s, it) => s + this.itemLineOldTotal(it), 0) ?? 0;
  }

  get subtotal(): number {
    return this.cartItems?.cartItems?.reduce((s, it) => s + this.itemLineTotal(it), 0) ?? 0;
  }

  get couponDiscount(): number {
    if (!this.appliedCoupon) return 0;

    const base = this.subtotal;
    const v = Number(this.appliedCoupon.value ?? 0);
    if (!v || v <= 0) return 0;

    if (this.appliedCoupon.type === 'percent') {
      const pct = Math.min(100, Math.max(0, v));
      return Math.round(base * (pct / 100));
    }
    return Math.min(base, v);
  }

  get total(): number {
    return Math.max(0, this.subtotal - this.couponDiscount);
  }

  get isEnoughBalance(): boolean {
    return this.userBalance >= this.total;
  }

  get balanceAfter(): number {
    return Math.max(0, this.userBalance - this.total);
  }
}
