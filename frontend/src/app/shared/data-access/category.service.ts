import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../enviroments/environment';import { Category } from '../models/cartegory.model';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private apiUrl = `${environment.apiBaseUrl}/admin/categories`;

  constructor(private http: HttpClient) {}

  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl);
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

  getCategoriesForLayout(): Observable<Category[]> {
    return this.http.get<Category[]>(`${environment.apiBaseUrl}/categories`);
  }
}
