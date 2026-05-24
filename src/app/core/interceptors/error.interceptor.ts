import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

export const errorInterceptor: HttpInterceptorFn = (request, next) => {
  const toast = inject(ToastService);
  const router = inject(Router);
  const auth = inject(AuthService);
  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && auth.refreshTokenValue && !request.url.includes('/api/auth/refresh') && !request.url.includes('/api/auth/login')) {
        return auth.refreshToken().pipe(
          switchMap((response) => next(request.clone({ setHeaders: { Authorization: `Bearer ${response.token}` } }))),
          catchError((refreshError: HttpErrorResponse) => {
            auth.logout();
            return throwError(() => refreshError);
          })
        );
      }
      const message = error.error?.message || error.error?.error || error.message || 'Request failed';
      if (error.status === 401) void router.navigateByUrl('/login');
      toast.show(message, 'error');
      return throwError(() => error);
    })
  );
};
