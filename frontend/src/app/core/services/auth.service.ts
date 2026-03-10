import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap ,BehaviorSubject, map} from 'rxjs';
import { AuthStorage } from '../../features/auth/auth.storage';
import { AuthSession } from '../../features/auth/auth.storage';
import { AuthUser } from '../../shared/models/core/user/user-auth.model';
import { UserService } from '../../shared/data-access/user.service';
import { ErrorPageComponent } from '../../shared/error-page/error-page.component';
import { environment } from '../../../../enviroments/environment';@Injectable({ providedIn: 'root' })
export class AuthService {

  private API = `${environment.apiBaseUrl}/auth`;
  private userSubject = new BehaviorSubject<AuthUser | null>(
    AuthStorage.get()?.user || null
  );
  user$ = this.userSubject.asObservable();
  constructor(private http: HttpClient) {
    const session = AuthStorage.get();
    if (session?.user) {
      this.userSubject.next(session.user);
    }
  }

  /* ================= LOGIN ================= */

  login(user: { username: string; password: string }) {
    return this.http.post<any>(`${this.API}/login`, user, {
      withCredentials: true
    }).pipe(
      tap(res => {
        AuthStorage.set({
          accessToken: res.accessToken,
          user: res.user
        });
        this.userSubject.next(res.user);
      })
    );
  }

  


  register(user: { username: string; email: string; password: string; retype_password: string }): Observable<any> { return this.http.post(`${this.API}/register`, user); }
  /* ================= USER ================= */

  setUser(user: AuthUser) {
    AuthStorage.setUser(user);
    this.userSubject.next(user);
  }

  refreshUser() {
    return this.http.get<any>(`${this.API}/me`, {
      withCredentials: true
    }).pipe(
      map(res => res.data as AuthUser),
      tap(user => {
        this.setUser(user);
      })
      
    );
  }
  loginWithGoogle(idToken: string) {
    return this.http
      .post<any>(
        `${this.API}/google-login`,
        { idToken },
        { withCredentials: true }
      )
      .pipe(
        tap(res => {
          if (!res?.data?.accessToken || !res?.data?.user) {
            throw new Error('Invalid google login response');
          }
          console.log('Google login response:', res);
          AuthStorage.set({ accessToken: res.data.accessToken, user: res.data.user });
          this.userSubject.next(res.user);
        })
      );
  }
  linkGoogleAccount(idToken: string) {
    return this.http
      .post<any>(
        `${this.API}/google-link`,
        { idToken },
        { withCredentials: true }
      )
      .pipe(
        tap(res => {
          console.log('Link Google response:', res);
        })
      );
  }

  getCurrentUser(): AuthUser | null {
    return this.userSubject.value;
  }
  getAccessToken(): string | null { return AuthStorage.get()?.accessToken ?? null; }

  /* ================= AUTH ================= */

  logout() {
    return this.http.post(`${this.API}/logout`, null, {
      withCredentials: true
    }).pipe(
      tap(() => {
        AuthStorage.clear();
        this.userSubject.next(null);
      })
    );
  }

  isLoggedIn(): boolean {
    return !!this.userSubject.value;
  }

  refreshToken() {
    return this.http.post(`${this.API}/fresh-token`, {}, {
      responseType: 'text',
      withCredentials: true
    }).pipe(
      tap(token => {
        const session = AuthStorage.get();
        if (session?.user && token) {
          AuthStorage.set({   
            accessToken: token,
            user: session.user
          });
        }   
      })
    );                                          
  }
}
