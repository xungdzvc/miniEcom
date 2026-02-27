import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface FaqItem {
  question: string;
  answer: string;
  open?: boolean;
}

@Component({
  selector: 'app-faq',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './faq.component.html',
  styleUrls: ['./faq.component.css']
})
export class FaqComponent {

  faqs: FaqItem[] = [
    {
      question: 'Mã nguồn có kèm hướng dẫn không?',
      answer: 'Có. Mỗi sản phẩm đều có tài liệu cài đặt chi tiết.'
    },
    {
      question: 'Có dùng cho dự án thương mại được không?',
      answer: 'Bạn có thể sử dụng cho dự án cá nhân hoặc thương mại.'
    },
    {
      question: 'Thanh toán như thế nào?',
      answer: 'Hỗ trợ QR, chuyển khoản ngân hàng tự động.'
    },
    {
      question: 'Sau khi mua có được hỗ trợ?',
      answer: 'Có. Hỗ trợ kỹ thuật 24/7 qua email hoặc Zalo.'
    }
  ];

  toggle(item: FaqItem){
    item.open = !item.open;
  }
}