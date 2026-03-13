import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl2 = `${environment.apiBaseUrl}/products`;
  constructor(private http: HttpClient) {}

  // Lấy danh sách sản phẩm
  
  getAllProductsForViewer(): Observable<any> {
    return this.http.get<any>(this.apiUrl2);
  }
 
 
  getProductByCategoryId(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl2}/category/${id}`);
  }
  getProductBySlug(slug: string) {
  return this.http.get<any>(`${this.apiUrl2}/slug/${slug}`);
  }
  updateViewProductBySlug(slug : string){
    return this.http.put<any>(`${this.apiUrl2}/slug/${slug}`,{});
  }


}

