import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthFormComponent } from '../shared/auth-form.component';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { NotificationService } from '../../../core/services/notification.service';
import { AuthLayoutComponent } from '../layout/auth-layout.component';
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, AuthFormComponent,AuthLayoutComponent],
  templateUrl: './login.component.html'
})
export class LoginComponent {

  form = {
    username: '',
    password: ''
  };

  errorMessage = '';

  constructor(private auth: AuthService, private router: Router,private noti :
    NotificationService
  ) {}

  onSubmit(data: any) {
    this.auth.login(this.form).subscribe({
      next: (err) => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.errorMessage =
          typeof err.error === 'string'
            ? err.error
            : err.error?.message || "Login failed!";
    }
    });
  }

  // Trong component login (nơi gọi onGoogleLogin)
  onGoogleLogin(idToken: string) {
    this.auth.loginWithGoogle(idToken).subscribe({
      next: (res) => {
        console.log('✅ Login success, navigating...');
        this.router.navigate(['/'], { 
          queryParams: { openMenu: 'true' } 
        }).then(success => {
          console.log('Navigation result:', success);
        });
      },
      error: (res) => {
        console.log('❌ Login failed:', res);
        this.noti.error(res.error?.message || "Đăng nhập bằng Google thất bại!");
      }
    });
  }

}
