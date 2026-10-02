import { Routes } from '@angular/router';
import { UserLayoutComponent } from './user-layout/user-layout.component';
import { authGuard } from '../../core/guards/auth.guard';

export const USER_ROUTES: Routes = [
  {
    
    path: '',
    component: UserLayoutComponent,
    children: [
      { path: 'home', loadComponent: () => import('./home/home.component').then(m => m.HomeComponent) },
      { path: '', loadComponent: () => import('./home/home.component').then(m => m.HomeComponent) },
      { path: 'products', loadChildren: () => import('./products/products.routes').then(m => m.PRODUCTS_ROUTES) },
      // placeholder routes (bạn làm UI sau)
      { path: 'cart', canActivate: [authGuard], loadComponent: () => import('./cart/cart.component').then(m => m.CartComponent) },
      { path: 'orders', canActivate: [authGuard], loadComponent: () => import('./orders/orders.component').then(m => m.OrdersComponent) },
      { path: 'order/:id', canActivate: [authGuard], loadComponent: () => import('./orders/order-detail/order-detail.component').then(m => m.OrderDetailComponent) },
      { path: 'profile', canActivate: [authGuard], loadComponent: () => import('./profile/profile.component').then(m => m.ProfileComponent) },
      { path: 'payment', canActivate: [authGuard], loadComponent: () => import('./payment/payment.component').then(m => m.PaymentComponent) },
    ],
  },
];
