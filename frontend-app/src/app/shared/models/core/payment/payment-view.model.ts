// models/payment-view.model.ts
export interface PaymentViewModel {
  type: 'ORDER' | 'TOPUP';

  // hiển thị
  title: string;
  amount: number;

  // xử lý chung
  expiresAt: string;
  qrCodeUrl: string;

  // dùng cho poll (optional)
  orderId?: number;
  topupId?: number;
}
