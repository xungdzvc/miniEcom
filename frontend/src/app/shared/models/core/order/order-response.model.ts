// models/order-checkout-response.model.ts
export interface OrderCheckoutResponse {
  orderId: number;
  status: string;
  total: number;
  expiresAt: string;   // ISO string từ BE
  qrcodeUrl: string;   // map từ QRCodeUrl
}
