// models/order-checkout-response.model.ts
export interface OrderResponse {
  orderId: number;
  status: string;
  total: number;
  orderDate: Date;
  
}
