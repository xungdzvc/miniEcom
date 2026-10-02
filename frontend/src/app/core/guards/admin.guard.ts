import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

function hasAdminRole(roles: unknown): boolean {
  if (Array.isArray(roles)) {
    return roles.some(role => String(role).replace(/^ROLE_/, '').toUpperCase() === 'ADMIN');
  }

  return String(roles ?? '')
    .split(/[\s,;|]+/)
    .filter(Boolean)
    .some(role => role.replace(/^ROLE_/, '').toUpperCase() === 'ADMIN');
}

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const user = auth.getCurrentUser();

  if (!user) {
    return router.createUrlTree(['/login'], { queryParams: { returnUrl: '/admin' } });
  }

  return hasAdminRole(user.roles)
    ? true
    : router.createUrlTree(['/error', 403]);
};
