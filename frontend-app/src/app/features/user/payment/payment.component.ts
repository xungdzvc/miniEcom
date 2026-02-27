import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { OrderCheckoutResponse } from '../../../shared/models/core/order/order-response.model';
import { OrderService } from '../../../shared/data-access/order.service';
import { PaymentViewModel } from '../../../shared/models/core/payment/payment-view.model';
import { PaymentService } from '../../../shared/data-access/payment.service';

@Component({
  standalone: true,
  selector: 'app-payment',
  imports: [CommonModule],
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent implements OnInit, OnDestroy {
  payment?: PaymentViewModel;
  paymentSuccess = false;
  // countdown
  remainingSeconds = 0;
  private timerId?: number;

  constructor(private router: Router,
    private orderService: OrderService,
    private paymentService: PaymentService
  ) {}

  private statusPollId?: number;

  ngOnInit(): void {
  const state = history.state;
  this.payment = state?.paymentView ?? null;
  if (!this.payment) {
    this.router.navigate(['/']);
    return;
  }

  this.startCountdown();
  this.startPollPaymentStatus();
}

  private startCountdown(): void {
  const expiresAt = new Date(this.payment!.expiresAt).getTime();

    const update = () => {
      const diff = Math.floor((expiresAt - Date.now()) / 1000);
      this.remainingSeconds = diff > 0 ? diff : 0;

      if (this.remainingSeconds === 0) {
        this.stopCountdown();
      }
    };

    update(); // ✅ TÍNH NGAY LẬP TỨC (QUAN TRỌNG)
    this.timerId = window.setInterval(update, 1000);
  }


  private stopCountdown(): void {
    if (this.timerId) {
      clearInterval(this.timerId);
      this.timerId = undefined;
    }
  }

  /** 👉 ĐÂY là thứ HTML cần */
  get remainingTime(): string {
  const total = this.remainingSeconds;

  const days = Math.floor(total / 86400);
  const hours = Math.floor((total % 86400) / 3600);
  const minutes = Math.floor((total % 3600) / 60);
  const seconds = total % 60;

  if (days > 0) {
    return `${days} ngày ${hours} giờ`;
  }

  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, '0')}`;
  }

  return `${minutes}:${seconds.toString().padStart(2, '0')}`;
}


  cancel(): void {
    this.router.navigate(['/orders']);
  }
  cancelTopup(): void {
    this.router.navigate(['/profile']);
  }


  continueShopping(): void {
    this.router.navigate(['/']);
  }

  get expiredAt(): Date | null {
    return this.payment?.expiresAt
      ? new Date(this.payment.expiresAt)
      : null;
  }

  

private startPollPaymentStatus(): void {
  const poll = () => {
    if (this.payment?.type === 'ORDER') {
      this.orderService
        .getPaymentStatus(this.payment.orderId!)
        .subscribe(status => this.handleStatus(status));
    }

    if (this.payment?.type === 'TOPUP') {
      
      this.paymentService
        .getTopupStatus(this.payment.topupId!)
        .subscribe(status => { this.handleStatus(status.data) });  
    }
  };

  poll();
  this.statusPollId = window.setInterval(poll, 2500);
}

private handleStatus(status: string) {
  console.log('Payment status:', status);
  if (status === 'SUCCESS') {
    this.stopCountdown();
    this.stopPoll();
    this.paymentSuccess = true;
  }

  if (status === 'EXPIRED') {
    this.stopCountdown();
    this.stopPoll();
  }
}
private stopPoll(): void {
  if (this.statusPollId) {
    clearInterval(this.statusPollId);
    this.statusPollId = undefined;
  }
}
goToWallet() {
  console.log('Navigating to wallet...');
  //this.router.navigate(['/home']);
}
ngOnDestroy(): void {
  this.stopCountdown();
  this.stopPoll();
}
}
