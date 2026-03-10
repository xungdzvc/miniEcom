import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { HomeComponent } from './home/home.component';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: DashboardComponent, // Layout Admin
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      },

      {
        path : 'home', component : HomeComponent
      },
     
      
     
      // QUẢN LÝ SẢN PHẨM
      {
        path: 'products',
        loadChildren: () =>
          import('./products/product.routes').then(r => r.PRODUCTS_ROUTES)
      },

      // QUẢN LÝ DANH MỤC
      {
        path: 'categories',
        loadChildren: () =>
          import('./categories/category.routes').then(r => r.CATEGORY_ROUTES)
      },

      {
        path: 'coupons',
        loadChildren: () =>
          import('./coupons/coupon.routes').then(r => r.COUPON_ROUTES)
      },

      // QUẢN LÝ USER
      {
        path: 'users',
        loadChildren: () =>
          import('./users/user.routes').then(r => r.USER_ROUTES)
      },
    ]
  }
];
