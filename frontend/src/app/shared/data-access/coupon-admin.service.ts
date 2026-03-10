import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../enviroments/environment';
import {
  CouponAdminRequest,
  CouponAdminResponse
} from './../models/coupon-admin.model';

@Injectable({
  providedIn: 'root'
})
export class CouponAdminService {
  private readonly api = environment.apiBaseUrl+'/admin/coupons';

  constructor(private http: HttpClient) {}

  getCoupons(): Observable<{ data: CouponAdminResponse[] }> {
    return this.http.get<{ data: CouponAdminResponse[] }>(this.api);
  }

  getCouponById(id: number): Observable<{ data: CouponAdminResponse }> {
    return this.http.get<{ data: CouponAdminResponse }>(`${this.api}/${id}`);
  }

  createCoupon(payload: CouponAdminRequest): Observable<any> {
    return this.http.post(this.api, payload);
  }

  updateCoupon(id: number, payload: CouponAdminRequest): Observable<any> {
    return this.http.put(`${this.api}/${id}`, payload);
  }

  deleteCoupon(id: number): Observable<any> {
    return this.http.delete(`${this.api}/${id}`);
  }
}