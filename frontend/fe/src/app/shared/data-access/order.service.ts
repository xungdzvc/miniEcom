import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CartResponse } from '../models/core/cart/cart.model';
import { Order } from '../models/core/checkout/order.model';
import { OrderResponse } from '../models/core/order/order-list.model';
import { PagedResponse } from '../models/paged-response.model';
import { environment } from '../../../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class OrderService{
    private API = `${environment.apiBaseUrl}/orders`;

    constructor(private http: HttpClient) {}

    pay(order :Order) :Observable<any>{
        return this.http.post<any>(`${this.API}/checkout`,order);
    }
    addProductToCart(slug:string) : Observable<any>{
        return this.http.post<any>(`${this.API}/add`,{slug});
    }
    cancelOrder(orderCode: string): Observable<any> {
        return this.http.delete<any>(`${this.API}/cancel/${orderCode}`);
    }
    getOrderByCode(orderCode: string): Observable<any> {
        return this.http.get<any>(`${this.API}/${orderCode}`);
    }
    getPaymentStatus(orderId: number): Observable<any> {
        return this.http.get<any>(`${this.API}/status/${orderId}`);
    }
    getUserOrders(): Observable<any[]> {
        return this.http.get<any>(`${this.API}`);
    }

    getUserOrdersPaged(page: number, size: number): Observable<PagedResponse<OrderResponse>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.http.get<PagedResponse<OrderResponse>>(`${this.API}`, { params });
    }

    getOrderDetail(orderId: number): Observable<any> {
        return this.http.get<any>(`${this.API}/${orderId}`);
    }
    downloadOrderItemFile(orderId: number, orderItemId: number): Observable<any> {
        return this.http.get<any>(`${this.API}/${orderId}/item/${orderItemId}/download`);
    }
    buyNowProduct(data :any): Observable<any> {
        return this.http.post<any>(`${this.API}/checkout/direct`, data);
    }

}