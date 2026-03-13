import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { environment } from '../../../enviroments/environment';import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class DashboardStateService {


  private apiUrl = `${environment.apiBaseUrl}/admin/home`;

  constructor(private http: HttpClient) {}



  getDashboardSummary(): Observable<any> {
      return this.http.get<any>(this.apiUrl);
  }


}
