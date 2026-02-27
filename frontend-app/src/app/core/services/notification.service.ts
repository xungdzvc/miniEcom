import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type NoticeType = 'success' | 'error' | 'info';

export type Notice = {
  type: NoticeType;
  message: string;
  ttlMs?: number;
};

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private _notice$ = new BehaviorSubject<Notice | null>(null);
  notice$ = this._notice$.asObservable();

  show(type: NoticeType, message: string, ttlMs = 2500) {
    this._notice$.next({ type, message, ttlMs });

    if (ttlMs > 0) {
      setTimeout(() => {
        // chỉ clear nếu vẫn là message đó (tránh clear nhầm)
        const current = this._notice$.value;
        if (current?.message === message && current?.type === type) {
          this.clear();
        }
      }, ttlMs);
    }
  }

  success(message: string, ttlMs = 2500) { this.show('success', message, ttlMs); }
  error(message: string, ttlMs = 3500) { this.show('error', message, ttlMs); }
  info(message: string, ttlMs = 2500) { this.show('info', message, ttlMs); }

  clear() {
    this._notice$.next(null);
  }
}
