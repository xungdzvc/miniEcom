import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {

  const auth = inject(AuthService);
  const router = inject(Router); // ✅ inject đúng cách

  const isAuthEndpoint =
    req.url.includes('/api/auth/refresh-token') ||
    req.url.includes('/api/auth/login') ||
    req.url.includes('/api/auth/register');

  // Các endpoint auth cần cookie
  if (isAuthEndpoint) {
    return next(req.clone({ withCredentials: true }));
  }

  const token = auth.getAccessToken();

  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError(error => {

      // ✅ 403 → redirect error page
      if (error.status === 403) {
        router.navigate(['/error', 403]);
        return throwError(() => error);
      }

      // ✅ chỉ refresh token khi 401
      if (error.status !== 401) {
        return throwError(() => error);
      }

      // 🔁 refresh token
      return auth.refreshToken().pipe(
        switchMap((newToken: string) => {

          if (!newToken) {
            auth.logout().subscribe();
            router.navigate(['/login']);
            return throwError(() => error);
          }

          return next(
            req.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken}`
              }
            })
          );
        }),
        catchError(refreshErr => {
          auth.logout().subscribe();
          router.navigate(['/login']);
          return throwError(() => refreshErr);
        })
      );
    })
  );
};
