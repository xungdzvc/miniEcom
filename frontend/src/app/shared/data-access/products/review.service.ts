import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../../enviroments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  private apiUrl = `${environment.apiBaseUrl}/products/reviews`;
  constructor(private http: HttpClient) {}

 
  getReviewByProductId(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/list`);
  }
  updateReviewByProductId(id : string,data : any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`,data);
  }
  checkCanRate(productId: number | undefined): Observable<boolean> {
   return this.http.get<boolean>(`${this.apiUrl}/can-rate/${productId}`);   
  } 
}