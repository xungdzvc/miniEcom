import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CouponAdminRequest,
  CouponAdminResponse
} from './../models/coupon-admin.model';
import { PagedResponse } from '../models/paged-response.model';

@Injectable({
  providedIn: 'root'
})
export class CouponAdminService {
  private readonly api = environment.apiBaseUrl+'/admin/coupons';

  constructor(private http: HttpClient) {}

  getCoupons(): Observable<{ data: CouponAdminResponse[] }> {
    return this.http.get<{ data: CouponAdminResponse[] }>(this.api);
  }

  getCouponsPaged(page: number, size: number): Observable<PagedResponse<CouponAdminResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedResponse<CouponAdminResponse>>(this.api, { params });
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