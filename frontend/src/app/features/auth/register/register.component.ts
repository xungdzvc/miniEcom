import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthFormComponent } from '../shared/auth-form.component';
import { AuthService } from '../../../core/services/auth.service';
import { AuthLayoutComponent } from '../layout/auth-layout.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, AuthFormComponent,AuthLayoutComponent],
  templateUrl: './register.component.html',
})
export class RegisterComponent {

  form = {
    username: '',
    email: '',
    password: '',
    retype_password: ''
  };

  errorMessage = '';

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  onSubmit(data: any) {
    this.auth.register(data).subscribe({
      next: (res) => {
        this.router.navigate(['/login']);  // Chuyển hướng về login
      },
      error: (err) => {
        this.errorMessage =
          typeof err.error === 'string'
            ? err.error
            : err.error?.message || "Registration failed!";
      }
    });

    console.log("Register:", data);
  }
}
