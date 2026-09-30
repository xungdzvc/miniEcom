import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { OrderService } from '../../../shared/data-access/order.service';
import { AuthService } from '../../../core/services/auth.service';
import { OrderResponse } from '../../../shared/models/core/order/order-list.model';
import { NotificationService } from '../../../core/services/notification.service';

type OrderStatus = 'SUCCESS' | 'PENDING' | 'FAILED' | 'REFUNDED';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
})
export class OrdersComponent implements OnInit {
  isLoading = true;
  q = '';
  status: 'ALL' | OrderStatus = 'ALL';
  todayYear = new Date().getFullYear();

  // pagination
  page = 1;
  pageSize = 5;
  totalPages = 1;

  orders: OrderResponse[] = [];

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private noti: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.isLoading = true;
    this.orderService.getUserOrdersPaged(this.page - 1, this.pageSize)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: res => {
          this.orders = res.content;
          this.totalPages = res.totalPages;
        },
        error: () => {
          this.noti.error('Chúng tôi gặp lỗi khi tải đơn hàng của bạn');
        }
      });
  }

  prevPage(): void {
    if (this.page > 1) {
      this.page--;
      this.loadOrders();
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages) {
      this.page++;
      this.loadOrders();
    }
  }

  goToOrderDetail(orderId: number): void {
    this.router.navigate([`/order/${orderId}`]);
  }

  label(status?: OrderStatus | string): string {
    status = status ?? 'UNKNOWN';
    switch (status) {
      case 'SUCCESS': return 'Đã thanh toán';
      case 'PENDING': return 'Chờ xử lý';
      case 'FAILED': return 'Thất bại';
      case 'REFUNDED': return 'Hoàn tiền';
      default: return 'Không rõ';
    }
  }
}
