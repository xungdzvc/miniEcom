import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap ,BehaviorSubject, map} from 'rxjs';
@Injectable({ providedIn: 'root' })
export class UserService {

  private API = 'http://localhost:8080/api/users';
  
  constructor(private http: HttpClient) {}

  
  getMe() : Observable<any>{
    return this.http.get(`${this.API}/me`);
  }
  updateUser(data : any): Observable<any>{
    return this.http.put<any>(`${this.API}/profile-edit`,data) ;
  }
  updatePassword(data : any): Observable<any>{
    return this.http.put<any>(`${this.API}/password-change`,data);
  }
  getTopupHistory(): Observable<any>{
    return this.http.get<any>(`${this.API}/topup-history`);
  }
  
}
  