import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, finalize, shareReplay, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

let refreshInFlight$: Observable<any> | null = null;

function isEndpoint(url: string, suffix: string): boolean {
  return url.includes(`/api/auth/${suffix}`);
}

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const isRefresh = isEndpoint(req.url, 'refresh-token');
  const skipBearer = [
    'login',
    'register',
    'refresh-token',
    'google-login',
    'logout',
    'forgot-password'
  ].some(path => isEndpoint(req.url, path));

  const token = auth.getAccessToken();
  const request = req.clone({
    withCredentials: skipBearer || req.withCredentials,
    setHeaders: !skipBearer && token
      ? { Authorization: `Bearer ${token}` }
      : {}
  });

  return next(request).pipe(
    catchError(error => {
      if (error.status === 403) {
        router.navigate(['/error', 403]);
        return throwError(() => error);
      }

      if (error.status !== 401 || skipBearer || isRefresh) {
        return throwError(() => error);
      }

      if (!refreshInFlight$) {
        refreshInFlight$ = auth.refreshToken().pipe(
          shareReplay({ bufferSize: 1, refCount: false }),
          finalize(() => {
            refreshInFlight$ = null;
          })
        );
      }

      return refreshInFlight$.pipe(
        switchMap(res => {
          const accessToken = res?.accessToken;

          if (!accessToken) {
            auth.clearLocalSession();
            router.navigate(['/login']);
            return throwError(() => error);
          }

          return next(req.clone({
            setHeaders: { Authorization: `Bearer ${accessToken}` }
          }));
        }),
        catchError(refreshError => {
          auth.clearLocalSession();
          router.navigate(['/login']);
          return throwError(() => refreshError);
        })
      );
    })
  );
};
