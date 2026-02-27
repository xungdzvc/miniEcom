import { Routes } from '@angular/router';
import { ErrorPageComponent } from './shared/error-page/error-page.component';
import { GoogleCallbackComponent } from './features/auth/login/google/googlecallback.component';

export const routes: Routes = [
  { path: 'error/:code', component: ErrorPageComponent },
  { path: 'error-page', component: ErrorPageComponent },
  { path: 'error', component: ErrorPageComponent },

  // Auth routes (lazy-load components)
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent),
  },
  {
    path: 'auth/google-callback',
    loadComponent: () => import('./features/auth/login/google/googlecallback.component').then(m => m.GoogleCallbackComponent),
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./features/auth/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent),
  },

  // Admin entry shortcut
  { path: 'dashboard', redirectTo: 'admin/home', pathMatch: 'full' },

  // User site
  {
    path: '',
    loadChildren: () => import('./features/user/user.routes').then(m => m.USER_ROUTES),
  },

  // Admin site
  {
    path: 'admin',
    loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES),
  },

  // { path: '**', redirectTo: 'login' },
];
