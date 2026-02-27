import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroments/environment';

@Injectable({
  providedIn: 'root'
})
export class CouponService{
    private apiUrl = `${environment.apiBaseUrl}/cart/coupon`;

    constructor(private http: HttpClient) {}

    getCouponCodeByCode(code : string){
        return this.http.post<any>(`${this.apiUrl}/${code}`,{});
    }
}