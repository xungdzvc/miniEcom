import {
  Component,
  EventEmitter,
  Input,
  Output,
  AfterViewInit,
  ElementRef,
  ViewChild
} from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { environment } from '../../../../../enviroments/environment';

@Component({
  selector: 'app-auth-form',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './auth-form.component.html',
  styleUrls: ['./auth-form.component.css'],
})
export class AuthFormComponent implements AfterViewInit {
  @Input() title = 'Login';
  @Input() buttonText = 'Sign In';
  @Input() mode: 'login' | 'register' = 'login';

  @Input() showLoginExtras = true;

  @Input() loginPath = '/login';
  @Input() registerPath = '/register';
  @Input() forgotPasswordPath = '/forgot-password';

  // link điều khoản/chính sách
  @Input() termsPath = '/terms';
  @Input() privacyPath = '/privacy';

  @Input() errorMessage = '';
  @Input() form: any = {};

  @Output() onSubmit = new EventEmitter<any>();
  @Output() googleLogin = new EventEmitter<string>();

  @ViewChild('googleBtn', { static: false })
  googleBtn!: ElementRef<HTMLDivElement>;

  // toggle show/hide password
  showPassword = false;
  showRetype = false;

  // register required: terms
  agreeTerms = false;

  ngAfterViewInit(): void {
    if (!this.googleBtn) return;
    this.createCustomGoogleButton();
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  toggleRetype() {
    this.showRetype = !this.showRetype;
  }

  passwordMismatch(): boolean {
    if (this.mode !== 'register') return false;
    const p = this.form?.password ?? '';
    const r = this.form?.retype_password ?? '';
    if (!p || !r) return false;
    return p !== r;
  }

  canSubmit(f: NgForm): boolean {
    if (!f?.valid) return false;

    if (this.mode === 'register') {
      if (!this.agreeTerms) return false;
      if (this.passwordMismatch()) return false;
    }

    return true;
  }

  submitForm(f: NgForm) {
    // mark all touched để hiện lỗi required
    f.form.markAllAsTouched();

    if (!this.canSubmit(f)) return;

    this.onSubmit.emit(this.form);
  }

  private createCustomGoogleButton(): void {
    const button = this.googleBtn.nativeElement;
    button.innerHTML = `
      <button type="button" style="
        display:flex;align-items:center;justify-content:center;gap:12px;
        padding:0 24px;border:1px solid #dadce0;border-radius:24px;background:white;cursor:pointer;
        font-family:'Roboto', sans-serif;font-size:14px;font-weight:500;color:#3c4043;
        width:280px;height:40px;transition:background-color .2s, box-shadow .2s;"
        onmouseover="this.style.backgroundColor='#f8f9fa'; this.style.boxShadow='0 1px 2px 0 rgba(60,64,67,.3), 0 1px 3px 1px rgba(60,64,67,.15)'"
        onmouseout="this.style.backgroundColor='white'; this.style.boxShadow='none'">
        <svg width="18" height="18" xmlns="http://www.w3.org/2000/svg">
          <g fill="none" fill-rule="evenodd">
            <path d="M17.6 9.2l-.1-1.8H9v3.4h4.8C13.6 12 13 13 12 13.6v2.2h3a8.8 8.8 0 0 0 2.6-6.6z" fill="#4285F4"/>
            <path d="M9 18c2.4 0 4.5-.8 6-2.2l-3-2.2a5.4 5.4 0 0 1-8-2.9H1V13a9 9 0 0 0 8 5z" fill="#34A853"/>
            <path d="M4 10.7a5.4 5.4 0 0 1 0-3.4V5H1a9 9 0 0 0 0 8l3-2.3z" fill="#FBBC05"/>
            <path d="M9 3.6c1.3 0 2.5.4 3.4 1.3L15 2.3A9 9 0 0 0 1 5l3 2.4a5.4 5.4 0 0 1 5-3.7z" fill="#EA4335"/>
          </g>
        </svg>
        <span>Đăng nhập với Google</span>
      </button>
    `;

    button.querySelector('button')?.addEventListener('click', () => {
      localStorage.setItem('google_auth_action', 'LOGIN');
      this.redirectToGoogleLogin();
    });
  }

  private redirectToGoogleLogin(): void {
    const redirectUri = `${window.location.origin}/auth/google-callback`;
    const params = new URLSearchParams({
      client_id: environment.googleClientId,
      redirect_uri: redirectUri,
      response_type: 'id_token',
      scope: 'openid email profile',
      nonce: Math.random().toString(36).substring(2),
      state: Math.random().toString(36).substring(2),
    });

    window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?${params.toString()}`;
  }
}