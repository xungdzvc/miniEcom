import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
@Component({
  selector: 'app-error-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="error-container">
      
      <div class="bg-text">{{ code }}</div>

      <div class="content-box">
        <h1 class="main-code">{{ code }}</h1>
        <h2 class="title">{{ getTitle() }}</h2>
        <p class="message">{{ getMessage() }}</p>
        
        <button class="btn-home" (click)="goBack()">
          <span class="icon">⬅</span>
          {{ buttonText }}
        </button>
      </div>

    </div>
  `,
  styleUrls: ['./error-page.component.css']
})
export class ErrorPageComponent {

  constructor(private router: Router, private route :ActivatedRoute) {}
  code: number | string = 403;
  @Input() buttonText = 'Quay về trang chủ';
  @Output() onAction = new EventEmitter<void>();

  ngOnInit() {
    this.route.queryParamMap.subscribe(param =>{
      const codeParam = param.get('code');
      if(codeParam){
        this.code = codeParam;
      }
    })
  }
  // Xử lý tiêu đề dựa trên mã lỗi
  getTitle(): string {
    switch (Number(this.code)) {
      case 403: return 'Truy cập bị từ chối';
      case 404: return 'Không tìm thấy trang';
      case 500: return 'Lỗi máy chủ';
      default: return 'Đã có lỗi xảy ra';
    }
  }

  // Xử lý nội dung mô tả
  getMessage(): string {
    switch (Number(this.code)) {
      case 403: return 'Rất tiếc, bạn không có quyền truy cập vào khu vực này.';
      case 404: return 'Đường dẫn bạn truy cập không tồn tại hoặc đã bị xóa.';
      case 500: return 'Hệ thống đang gặp sự cố. Vui lòng thử lại sau.';
      default: return 'Vui lòng kiểm tra lại đường truyền hoặc liên hệ quản trị viên.';
    }
  }

  goBack() {
    this.onAction.emit();
    this.router.navigate(['admin/home']);
  }
}