import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  currentYear = new Date().getFullYear();

  exploreLinks = [
    { label: 'Trang chủ', link: '/' },
    { label: 'Sản phẩm', link: '/products' },
    { label: 'Liên hệ', link: '/contact' },
    { label: 'Cộng đồng', link: '/community' } // Thêm mới cho "đẳng cấp"
  ];

  accountLinks = [
    { label: 'Tài khoản', link: '/account' },
    { label: 'Nạp tiền', link: '/deposit' },
    { label: 'Lịch sử mua', link: '/orders' }
  ];
}