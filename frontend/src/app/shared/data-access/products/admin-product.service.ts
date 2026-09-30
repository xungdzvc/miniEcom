import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ProductAdminList } from '../../models/core/product/product-admin-list.model';
import { ProductAddOrUpdate } from '../../models/core/product/product-add-or-update.model';

@Injectable({
  providedIn: 'root'
})
export class ProductAdminService {

  private apiUrl = `${environment.apiBaseUrl}/admin/products`;
  constructor(private http: HttpClient) {}

  // Lấy danh sách sản phẩm
  getAllProducts(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }
  getAllProductsPaged(page: number, size: number): Observable<any> {
    const params = {
      page: page.toString(),
      size: size.toString()
    };
    return this.http.get<any>(this.apiUrl, { params });
  }
  rebuildElastic(): Observable<any> { 
    return this.http.post(`${this.apiUrl}/elastic`, {},{ responseType: 'text' });
  }
  deleteProduct(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
  updateProduct(id : number , data : any): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}`,data);
  }
  changeStatus(id : number , data : any): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/status`,data);
  }
  pin(id : number , data : any): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/pin`,data);
  }
  addProduct(data : any): Observable<any> {
    return this.http.post(this.apiUrl,data);
  }
  uploadS3(file: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('file', file);  
    return this.http.post<{ url: string }>(`${environment.apiBaseUrl}/admin/uploads`, formData);
  }
  getProductById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }
 


}

