import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Category } from '../models/cartegory.model';
import { PagedResponse } from '../models/paged-response.model';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private apiUrl = `${environment.apiBaseUrl}/admin/categories`;

  constructor(private http: HttpClient) {}

  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl);
  }

  getAllCategoriesPaged(page: number, size: number): Observable<PagedResponse<Category>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedResponse<Category>>(this.apiUrl, { params });
  }

  addCategory(data : any): Observable<any> {
    return this.http.post(this.apiUrl,data);
  }

  updateCategory(id : number , data : any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`,data);
  }

  deleteCategory(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.apiUrl}/${id}`);
  }

  getCategoriesForLayout(): Observable<{ data: Category[] }> {
    return this.http.get<{ data: Category[] }>(`${environment.apiBaseUrl}/categories`);
  }
}
