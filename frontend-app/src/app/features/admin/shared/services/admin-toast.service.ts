import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type AdminToastType = 'success' | 'error' | 'info' | 'warning';

export interface AdminToast {
  id: string;
  type: AdminToastType;
  message: string;
  title?: string;
  timeoutMs: number;
  createdAt: number;
}

function genId(): string {
  const rand = Math.random().toString(16).slice(2);
  return Date.now().toString() + '_' + rand;
}

@Injectable({
  providedIn: 'root',
})
export class AdminToastService {
  private readonly toastsSubject = new BehaviorSubject<AdminToast[]>([]);
  readonly toasts$ = this.toastsSubject.asObservable();

  show(message: string, opts?: Partial<Omit<AdminToast, 'id' | 'message' | 'createdAt'>>): string {
    const toast: AdminToast = {
      id: genId(),
      type: opts?.type ?? 'info',
      title: opts?.title,
      message,
      timeoutMs: opts?.timeoutMs ?? 3500,
      createdAt: Date.now(),
    };

    const current = this.toastsSubject.getValue();
    this.toastsSubject.next([toast, ...current].slice(0, 5));

    if (toast.timeoutMs > 0) {
      setTimeout(() => this.dismiss(toast.id), toast.timeoutMs);
    }

    return toast.id;
  }

  success(message: string, title = 'Thành công') {
    return this.show(message, { type: 'success', title });
  }

  error(message: string, title = 'Lỗi') {
    return this.show(message, { type: 'error', title, timeoutMs: 5000 });
  }

  warning(message: string, title = 'Cảnh báo') {
    return this.show(message, { type: 'warning', title, timeoutMs: 4500 });
  }

  info(message: string, title = 'Thông báo') {
    return this.show(message, { type: 'info', title });
  }

  dismiss(id: string) {
    const next = this.toastsSubject.getValue().filter(t => t.id !== id);
    this.toastsSubject.next(next);
  }

  clear() {
    this.toastsSubject.next([]);
  }
}
