import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap ,BehaviorSubject, map} from 'rxjs';
import { environment } from '../../../environments/environment';
@Injectable({ providedIn: 'root' })
export class RoleService {

  private API = `${environment.apiBaseUrl}/admin/roles`;
  
  constructor(private http: HttpClient) {}

  
  getAllRoles() : Observable<any>{
    return this.http.get(`${this.API}`);
  }
  
}
  