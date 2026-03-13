import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap ,BehaviorSubject, map} from 'rxjs';
import { environment } from '../../../enviroments/environment';
@Injectable({ providedIn: 'root' })
export class UserService {

  private API = `${environment.apiBaseUrl}/users`;
  
  constructor(private http: HttpClient) {}

  
  getMe() : Observable<any>{
    return this.http.get(`${this.API}/me`);
  }
  updateProfile(data : any): Observable<any>{
    return this.http.put<any>(`${this.API}/profile-edit`,data) ;
  }
  updatePassword(data : any): Observable<any>{
    return this.http.put<any>(`${this.API}/password-change`,data);
  }
  getTopupHistory(): Observable<any>{
    return this.http.get<any>(`${this.API}/topup-history`);
  }
  

  
}
  