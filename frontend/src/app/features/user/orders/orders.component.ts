import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { OrderService } from '../../../shared/data-access/order.service';
import { AuthService } from '../../../core/services/auth.service';
import { OrderResponse } from '../../../shared/models/core/order/order-list.model';
import { NotificationService } from '../../../core/services/notification.service';

type OrderStatus = 'SUCCESS' | 'PENDING' | 'FAILED' | 'REFUNDED';

type OrderRow = {
  code: string;
  createdAt: string;
  total: number;
  itemsCount: number;
  status: OrderStatus;
};

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
})
export class OrdersComponent implements OnInit  {
  q = '';
  status: 'ALL' | OrderStatus = 'ALL';
  todayYear = new Date().getFullYear();
  constructor(private orderService: OrderService,
    private authService: AuthService,
    private noti: NotificationService,
    private router: Router
  ) { }

  orders: OrderResponse[] = [];
  ngOnInit(): void {
    this.loadOrders();
  }


  loadOrders(): void {
    this.orderService.getUserOrders().subscribe((res: any) => {
      this.orders = res?.data ?? [];
    }, error => {
      this.noti.error(error?.error?.message ?? 'Chúng tôi gặp lỗi khi tải đơn hàng của bạn');
    }
  );
  }

  goToOrderDetail(orderId: number): void {
    this.router.navigate([`/order/${orderId}/detail`]);
  }

  get filtered(): OrderResponse[] {
  const keyword = this.q.trim().toLowerCase();

  return (this.orders ?? []).filter(o => {
    const okStatus = this.status === 'ALL' ? true : o.status === this.status;

    if (!keyword) return okStatus;

    const haystack = [
      o.orderId?.toString(),
      o.status,
      o.total?.toString(),
      o.orderDate
    ].filter(Boolean).join(' ').toLowerCase();

    return okStatus && haystack.includes(keyword);
  });
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
