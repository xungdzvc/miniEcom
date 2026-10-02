import { Routes } from '@angular/router';
import { CouponFormComponent } from './pages/coupon-form/coupon-form.component';
import { CouponListComponent } from './pages/coupon-list/coupon-list.component';
export const COUPON_ROUTES: Routes = [
  { path: '', component: CouponListComponent },
  { path: 'add', component: CouponFormComponent },
  { path: ':id', component: CouponFormComponent }
];
