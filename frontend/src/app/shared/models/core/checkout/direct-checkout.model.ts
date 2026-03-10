export interface DirectCheckoutRequest {
  productId: number;
  quantity: number;
  couponCode?: string;
  paymentMethod: 'WALLET' | 'ORDER_BANKING';
}