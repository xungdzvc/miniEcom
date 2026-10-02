import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-wrap" *ngIf="(ns.notice$ | async) as n">
      <div class="toast"
        [class.success]="n.type==='success'"
        [class.error]="n.type==='error'"
        [class.info]="n.type==='info'"
        [class.closing]="closing"
      >
        <div class="msg">{{ n.message }}</div>
        <button class="x" type="button" (click)="close()">✕</button>
      </div>
    </div>
  `,
  styleUrls: ['./notification.component.css'],
})
export class NotificationComponent {
  closing = false;
  private closeTimer: any;

  constructor(public ns: NotificationService) {}

  close() {
    if (this.closing) return;
    this.closing = true;

    // chờ animation out xong rồi mới clear
    clearTimeout(this.closeTimer);
    this.closeTimer = setTimeout(() => {
      this.ns.clear();
      this.closing = false;
    }, 180); // phải khớp với toastOut 180ms
  }
}
