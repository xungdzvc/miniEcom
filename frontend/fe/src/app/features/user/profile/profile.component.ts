import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ThemeService, ThemeMode } from '../../../core/services/theme.service';
import { AuthService } from '../../../core/services/auth.service';
import { AuthUser } from '../../../shared/models/core/user/user-auth.model';
import { ProfileUser } from '../../../shared/models/core/user/user-profile.model';
import { UserService } from '../../../shared/data-access/user.service';
import { tap } from 'rxjs';
import { NotificationService } from '../../../core/services/notification.service';
import { topupResponse } from '../../../shared/models/core/payment/topup-model';
import { environment } from '../../../../environments/environment';import { PaymentService } from '../../../shared/data-access/payment.service';
import { PaymentViewModel } from '../../../shared/models/core/payment/payment-view.model';

type AccountMenu =
  | 'profile'
  | 'password'
  | 'wallet'
  | 'topup-history'
  | 'orders'
  | 'notifications'
  | 'tools'
  | 'theme';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent  implements  OnInit {
    constructor(private themeService: ThemeService,
      private auth : AuthService,
      private user : UserService,
      private notifiService : NotificationService,
      private router : Router,
      private paymentService : PaymentService
    ) {
      this.loadProfile();
    this.themeMode = this.themeService.getMode();
  }

  topupsHistory: topupResponse[] = [];
  successMessage = "";
  errorMessage = "";
  isChanged = false;
  isLoadProfile =  false;
  profile: AuthUser | null = null;
  menu: AccountMenu = 'profile';
  themeMode: ThemeMode = 'system';
  walletMethod: 'ATM' | 'CARD' = 'ATM';
  googleLinked = false;
  googleId = '';
  isShowModal = false;
  amount = 0; 
  topup : topupResponse | null = null;
  card = {
    telco: '',
    amount: '',
    code: '',
    serial: ''
  };
  form = {
    username: '',
    fullName: '',
    email: '',
    phoneNumber: '',
    
  };

  formPw = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  }




  ngOnInit() {
    this.auth.user$.subscribe(user => {
      if (user) {
        this.profile = user;
        this.googleLinked = user.googleId ? true : false;
        this.googleId = user.googleId ?? '';
        this.fillForm();
      }
    });
  }
  topupWithBankTransfer() {
    if (!this.amount || this.amount < 10000) {
      this.notifiService.error("Số tiền nạp tối thiểu là 10.000đ");
      return;
    }
    this.paymentService.createTopUp(this.amount).subscribe({
      next: (res) => {
        console.log(res.data);
        const topup = res.data;
        const paymentView: PaymentViewModel = {
        type: 'TOPUP',
        title: 'Nạp tiền chuyển khoản',
        amount: topup.amount,
        expiresAt: topup.expiresAt,
        qrCodeUrl: topup.qrcodeUrl,
        topupId: topup.topupId
      };
        this.notifiService.success("Tạo yêu cầu nạp tiền thành công");
        this.router.navigate(['/payment'], { state: { paymentView } });
 
        this.isShowModal = false;
        this.amount = 0;
      },
      error: (err) => {
        this.notifiService.error("Tạo yêu cầu nạp tiền thất bại: " + err);
      }
    });
    this.isShowModal = true;
  }

  topupWithCard(form: any) {
    if (form.invalid) {
    return;
    }
    console.log('Nạp thẻ:', this.card);
    this.paymentService.chargeWithCard({
      loaiThe: this.card.telco,
      menhGia: Number(this.card.amount),
      seri: this.card.serial,
      maThe: this.card.code 
    }).subscribe({
      next : (res) =>{
          
          this.notifiService.success("Nạp thẻ thành công");
          this.card = {
            telco: '',
            amount: '',
            code: '',
            serial: ''
          };
          this.loadTopups();
      },
      error : (err) =>{
          this.notifiService.error("Nạp thẻ thất bại: " + err);
        
        }
    })
    // this.walletMethod = 'CARD';
    // this.router.navigate(['/user/wallet/topup'], { queryParams: { method: 'CARD' } });
  }

  setMenu(m: AccountMenu) {
    this.menu = m;
    if(m === 'profile') this.loadProfile();
    if(m === 'topup-history') this.loadTopups();
    if(m === 'orders') this.router.navigate(['/orders']);;
  }
  setTheme(mode: ThemeMode) {
    this.themeMode = mode;
    this.themeService.setMode(mode);
  }

  loadTopups(){
    this.user.getTopupHistory().subscribe({
      next : (res)=>{
        this.topupsHistory = res.data;
        
      },
      error : (err) =>{
          const code = err?.status ?? 500;

          this.router.navigate(['/error', code]);
        
        }
    })
  }
  linkGoogle(){
    if(this.googleLinked){
      this.notifiService.info("Tài khoản đã được liên kết với Google");
      return;
    }
    localStorage.setItem('google_auth_action', 'LINK');
    this.redirectToGoogle();
    
  }
  private redirectToGoogle(): void {
    const redirectUri = `${window.location.origin}/auth/google-callback`;
    const params = new URLSearchParams({
      client_id: environment.googleClientId,
      redirect_uri: redirectUri,
      response_type: 'id_token', // Hoặc 'code' tùy luồng bạn dùng
      scope: 'openid email profile',
      nonce: Math.random().toString(36).substring(2),
      state: Math.random().toString(36).substring(2)
    });
    
    window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?${params.toString()}`;
}
  loadProfile(){
    this.auth.refreshUser().subscribe();
  }
  onFormChange() {
  this.isChanged = true;
  }

  updateProfile(){
    if(!this.profile) return;
    const data :any = {

    }
    if(this.form.fullName.trim()){
      data.fullName = this.form.fullName;
    }
    if(this.form.phoneNumber.trim()){
      data.phoneNumber = this.form.phoneNumber;
      
    }
    if(this.form.phoneNumber && this.form.fullName
    ){
      this.notifiService.error("Vui lòng điền đầy đủ thông tin")
    }
    this.user.updateProfile(data).subscribe({
      next : res =>{
          this.auth.setUser(res.data);
          this.fillForm(); 
          this.notifiService.success("Cập nhật thành công ")
      },error: err => {
        this.notifiService.error(err);
      }

    })
    
  }

  updatePassword(){
    if(!this.profile) return;
    const data :any = {

    }
    if(this.formPw.oldPassword.trim()){
      data.oldPassword = this.formPw.oldPassword;
    }
    if(this.formPw.newPassword.trim()){
      data.newPassword = this.formPw.newPassword;
      
    }
    if(this.formPw.confirmPassword.trim()){
      data.confirmPassword = this.formPw.confirmPassword;
    }
    if(this.formPw.newPassword 
      && this.formPw.newPassword != this.formPw.confirmPassword
    ){
      this.notifiService.error("Mật khẩu xác nhận không khớp")
    }
    this.user.updatePassword(data).subscribe({
      next : res =>{
          this.auth.setUser(res.data);
          this.fillForm();
          this.notifiService.success("Cập nhật thành công")
      },error: err => {
        this.errorMessage = err;
        this.notifiService.error(err);
      }

    })
  }
  fillForm(){
    if(!this.profile) return;
    this.form = {
      ...this.form,
        username : this.profile.username,
        fullName : this.profile.fullName,
        email : this.profile.email,
        phoneNumber : this.profile.phoneNumber

    }
    
  }
}
