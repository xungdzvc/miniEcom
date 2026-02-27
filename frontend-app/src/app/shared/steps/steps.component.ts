import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface StepItem {
  title: string;
  description: string;
  icon: string;
}

@Component({
  selector: 'app-steps',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './steps.component.html',
  styleUrls: ['./steps.component.css']
})
export class StepsComponent {

  steps: StepItem[] = [
    {
      title: 'Chọn mã nguồn',
      description: 'Tìm kiếm theo danh mục hoặc từ khóa phù hợp với nhu cầu.',
      icon: '🔍'
    },
    {
      title: 'Xem demo & preview',
      description: 'Xem giao diện và tài liệu trước khi quyết định mua.',
      icon: '🖥️'
    },
    {
      title: 'Thanh toán tự động',
      description: 'Hỗ trợ QR, chuyển khoản, xử lý ngay lập tức.',
      icon: '💳'
    },
    {
      title: 'Tải về & triển khai',
      description: 'Nhận source và hướng dẫn setup chi tiết.',
      icon: '🚀'
    }
  ];
}