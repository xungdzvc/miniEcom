import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PagedResponse } from '../../models/paged-response.model';
import { ProductViewerDetail } from '../../models/core/product/product-viewer-detail.model';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl2 = `${environment.apiBaseUrl}/products`;
  constructor(private http: HttpClient) {}

  getAllProductsForViewer(page: number, size: number): Observable<PagedResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http
      .get<ApiResponse<PagedResponse<any>>>(this.apiUrl2, { params })
      .pipe(map(res => res.data));
  }

  getProductByCategoryId(id: number, page: number, size: number): Observable<PagedResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http
      .get<ApiResponse<PagedResponse<any>>>(`${this.apiUrl2}/category/${id}`, { params })
      .pipe(map(res => res.data));
  }

  getProductBySlug(slug: string): Observable<ProductViewerDetail> {
    return this.http
      .get<ApiResponse<ProductViewerDetail>>(`${this.apiUrl2}/${slug}`)
      .pipe(map(res => res.data));
  }

  updateViewProductBySlug(slug : string){
    return this.http.patch<any>(`${this.apiUrl2}/${slug}`,{});
  }
}
