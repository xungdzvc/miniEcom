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
    { label: 'Liên hệ', link: 'https://zalo.me/0388001659' },
    { label: 'Cộng đồng', link: 'https://zalo.me/g/psrero125' } // Thêm mới cho "đẳng cấp"
  ];

  accountLinks = [
    { label: 'Tài khoản', link: '/profile' },
    { label: 'Nạp tiền', link: '/profile' },
    { label: 'Lịch sử mua', link: '/orders' }
  ];
}