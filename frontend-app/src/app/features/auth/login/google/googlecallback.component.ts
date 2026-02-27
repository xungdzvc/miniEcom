import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-google-callback',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="display: flex; justify-content: center; align-items: center; height: 100vh;">
      <div style="text-align: center;">
        <div class="spinner"></div>
        <p style="margin-top: 20px;">Đang xử lý đăng nhập...</p>
      </div>
    </div>
  `,
  styles: [`
    .spinner {
      border: 4px solid #f3f3f3;
      border-top: 4px solid #3498db;
      border-radius: 50%;
      width: 40px;
      height: 40px;
      animation: spin 1s linear infinite;
      margin: 0 auto;
    }
    
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
  `]
})
export class GoogleCallbackComponent implements OnInit {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    // Parse URL fragment (sau dấu #)
    const hash = window.location.hash.substring(1);
    const params = new URLSearchParams(hash);
    const idToken = params.get('id_token');

    console.log('Hash:', hash);
    console.log('ID Token:', idToken);

    if (!idToken) {
      console.error('Không tìm thấy id_token');
      this.router.navigate(['/login']);
      return;
    }

    const action = localStorage.getItem('google_auth_action');
    if(action === 'LINK'){
      this.linkGoogleAccount(idToken);
    }else{
      this.loginWithGoogle(idToken);
    }
    
  }

  loginWithGoogle(idToken: string) {
    this.authService.loginWithGoogle(idToken).subscribe({
      next: () => {
        localStorage.removeItem('google_auth_action');
        window.location.href = '/';
      },
      error: () => {
        this.router.navigate(['/login']);
      }
    });
  }

  linkGoogleAccount(idToken: string) {
    this.authService.linkGoogleAccount(idToken).subscribe({
      next: () => {
        localStorage.removeItem('google_auth_action');
        window.location.href = '/profile';
      },
      error: () => {
        this.router.navigate(['/profile']);
      }
    });
  }
}