import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './auth-layout.component.html',
  styleUrls: ['./auth-layout.component.css'],
})
export class AuthLayoutComponent {
  @Input() brand = 'TuanBinh Enterprise';
  @Input() badgeText = 'Trusted Platform';

  // nội dung bên trái (bạn có thể đổi tuỳ shop)
  @Input() headline = 'Mua sắm nhanh – Giá tốt – Giao hàng chuẩn';
  @Input() subheadline =
    'Đăng nhập để theo dõi đơn hàng, lưu sản phẩm yêu thích và nhận ưu đãi độc quyền.';
  @Input() bullets: string[] = [
    'Ưu đãi mỗi ngày, voucher theo hạng thành viên',
    'Thanh toán an toàn, hỗ trợ đổi trả linh hoạt',
    'Theo dõi đơn hàng realtime, CSKH 24/7',
  ];
}