import { Injectable } from "@angular/core";
import { environment } from '../../../enviroments/environment';
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
@Injectable({
  providedIn: 'root'
})

export class UserAdminService{
    private apiUrl = `${environment.apiBaseUrl}/admin/users`;

    constructor(private http: HttpClient) {}

    getUsers():Observable<any>{
        return this.http.get<any>(this.apiUrl);
    }

    getUserById(userId : number):Observable<any>{
        return this.http.get(`${this.apiUrl}/${userId}`);
    }
    
    changeStatus(userId : number, status :boolean):Observable<void>{
        return this.http.put<void>(`${this.apiUrl}/${userId}/status`,{status});
    }
    makeStaff(userId : number, staff :boolean):Observable<void>{
        return this.http.put<void>(`${this.apiUrl}/${userId}/staff`,{staff});
    }
    removeStaff(userId : number, staff :boolean):Observable<void>{
        return this.http.put<void>(`${this.apiUrl}/${userId}/staff`,{staff});
    }
    updateUser(id : number , data : any): Observable<any>{
    return this.http.put(`${this.apiUrl}/${id}`,data);
    }
    createUser(data : any): Observable<any>{
        return this.http.post(`${this.apiUrl}`,data);
    }
}