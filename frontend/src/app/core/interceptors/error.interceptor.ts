import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { NotificationService } from '../services/notification.service';

@Injectable()
export class ApiErrorInterceptor implements HttpInterceptor {
  constructor(private router: Router, private notify: NotificationService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    req = req.clone({
    withCredentials: true // <--- Luôn gửi kèm cookie
  });
    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        const code = err.status;

        // lỗi nhỏ: toast
        if (code === 0) {
          this.notify.error('Không thể kết nối máy chủ.');
          return throwError(() => err);
        }

        // lỗi cần chuyển page
        if (code === 403 || code === 404 || code >= 500) {
          this.router.navigate(['/error', code]);
          return throwError(() => err);
        }

        // còn lại: toast
        this.notify.error(err.error?.message || 'Có lỗi xảy ra.');
        return throwError(() => err);
      })
    );
  }
}
