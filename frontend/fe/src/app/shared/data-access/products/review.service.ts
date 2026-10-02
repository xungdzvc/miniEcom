import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PagedResponse } from '../../models/paged-response.model';
import { ReviewResponse } from '../../models/review-list.models';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  private apiUrl = `${environment.apiBaseUrl}/reviews`;
  constructor(private http: HttpClient) {}

  getReviewByProductId(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/list`);
  }

  getReviewByProductIdPaged(id: number, page: number, size: number): Observable<PagedResponse<ReviewResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedResponse<ReviewResponse>>(`${this.apiUrl}/${id}/list`, { params });
  }

  updateReviewByProductId(id : string,data : any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${id}`,data);
  }
  checkCanRate(productId: number | undefined): Observable<boolean> {
   return this.http.get<boolean>(`${this.apiUrl}/${productId}/can-rate`);
  }
}