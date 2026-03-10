import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { OrderDetailResponse } from '../../../../shared/models/core/order/order-detail.model'; // chỉnh path theo project của bạn
import { OrderService } from '../../../../shared/data-access/order.service';
import { NotificationService } from '../../../../core/services/notification.service';
@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.css'],
})
export class OrderDetailComponent implements OnInit, OnDestroy {
  isLoading = false;
  isDownloadingId: number | null = null;

  orderId = 0;
  order?: OrderDetailResponse;

  year = new Date().getFullYear();
  keyword = '';

  private sub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private noti: NotificationService 
  ) {}

  ngOnInit(): void {
    this.orderId = Number(this.route.snapshot.paramMap.get('id') ?? 0);
    if (!this.orderId) {
      this.router.navigate(['/orders']);
      return;
    }
    this.fetch();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  fetch(): void {
    this.isLoading = true;
    this.sub?.unsubscribe();
    this.sub = this.orderService.getOrderDetail(this.orderId).subscribe({
      next: (res) => {
        this.order = res.data;
        this.isLoading = false;
        },
        error: (err) => {
          this.isLoading = false;
          this.noti.error(err?.error?.message ?? 'Chúng tôi gặp lỗi khi tải chi tiết đơn hàng của bạn');
        },
      });
  }

  goBack(): void {
    this.router.navigate(['/orders']);
  }

  orderCode(): string {
    return `HD${this.year}${this.orderId}`;
  }

  canDownload(): boolean {
    return this.order?.status === 'SUCCESS';
  }

  // Vì field của bạn tên "OrderItemResponses"
  items() {
    return this.order?.orderItems ?? [];
  }

  filteredItems() {
    const items = this.items();
    const kw = this.keyword.trim().toLowerCase();
    if (!kw) return items;

    return items.filter((x: any) => {
      const name = String(x.productName ?? '').toLowerCase();
      const cat = String(x.categoryName ?? '').toLowerCase();
      const pid = String(x.productId ?? '');
      return name.includes(kw) || cat.includes(kw) || pid.includes(kw);
    });
  }

  // tính tổng dòng (nếu backend không trả lineTotal)
  lineTotal(it: any): number {
    const price = Number(it.price ?? 0);
    const qty = Number(it.quantity ?? 0);
    return price * qty;
  }

  statusText(status?: string): string {
    switch (status) {
      case 'SUCCESS': return 'Đã thanh toán';
      case 'PENDING': return 'Chờ thanh toán';
      case 'FAILED': return 'Thất bại';
      case 'CANCELLED': return 'Đã huỷ';
      case 'EXPIRED': return 'Hết hạn';
      default: return 'Không rõ';
    }
  }

  statusClass(status?: string): string {
    switch (status) {
      case 'SUCCESS': return 'badge success';
      case 'PENDING': return 'badge warning';
      case 'FAILED': return 'badge danger';
      default: return 'badge muted';
    }
  }

  download(orderItemId: number): void {
  if (!this.order) return;
  if (!this.canDownload()) {
    alert('Đơn hàng chưa thanh toán thành công. Không thể tải xuống.');
    return;
  }

  this.isDownloadingId = orderItemId;

  this.orderService.downloadOrderItemFile(this.orderId, this.isDownloadingId).subscribe({
    next: (res) => {
      const url = res?.data;
      if (!url) {
        this.noti.error('Không nhận được link tải xuống.');
        this.isDownloadingId = null;
        return;
      }
      window.open(url, '_blank');
      this.isDownloadingId = null;
    },
    error: (err) => {
      this.noti.error(err?.error?.message ?? 'Chúng tôi gặp lỗi khi tải file sản phẩm.');
      this.isDownloadingId = null;
    },
  });
}
  
}
