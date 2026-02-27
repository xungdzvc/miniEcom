import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap ,BehaviorSubject, map} from 'rxjs';
import { CartResponse } from '../models/core/cart/cart.model';
import { Order } from '../models/core/checkout/order.model';
import { OrderResponse } from '../models/core/order/order-list.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService{
    private API = 'http://localhost:8080/api/order';

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

    getOrderDetail(orderId: number): Observable<any> {
        return this.http.get<any>(`${this.API}/${orderId}/detail`);
    }
    downloadOrderItemFile(orderId: number, orderItemId: number): Observable<any> {
        return this.http.get<any>(`${this.API}/${orderId}/item/${orderItemId}/download`);
    }
    buyNowProduct(data :any): Observable<any> {
        return this.http.post<any>(`${this.API}/checkout/direct`, data);
    }

}