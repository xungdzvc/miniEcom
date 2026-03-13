import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap ,BehaviorSubject, map} from 'rxjs';
import { CartResponse } from '../models/core/cart/cart.model';
import { environment } from '../../../environments/environment';@Injectable({
  providedIn: 'root'
})
export class CartService{
    private API = `${environment.apiBaseUrl}/cart`;
    private cartCountSubject = new BehaviorSubject<number>(0);
    cartCount$ = this.cartCountSubject.asObservable();
    constructor(private http: HttpClient) {}

    getCart() :Observable<any>{
        return this.http.get<any>(`${this.API}`);
    }
    addProductToCart(slug:string) : Observable<any>{
        return this.http.post<any>(`${this.API}/add`,{slug});
    }
    updateQty(cartItemId:number, quantity:number) : Observable<any>{
        return this.http.put<any>(`${this.API}/update-qty`,{cartItemId, quantity});
    }
    removeProductFromCart(cartItemId:number) : Observable<any>{
        return this.http.delete<any>(`${this.API}/remove/${cartItemId}`);
    }
    clearCart() : Observable<any>{
        return this.http.delete<any>(`${this.API}/clear`);
    }
    getCartItemCount(): Observable<number> {
        return this.getCart().pipe(
            map(res => res?.data?.cartItems?.length ?? 0)
        );
    }
    refreshCartCount() {
        return this.getCartItemCount().pipe(
        tap(count => this.cartCountSubject.next(count))
        );
    }
}