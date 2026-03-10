import { OrderItemResponse } from './order-item.model';
export interface OrderDetailResponse {
  id: number;
  status: 'PENDING' | 'SUCCESS' | 'FAILED' | 'CANCELLED' | 'EXPIRED';
  createdAt: string; // ISO
  total: number;
  orderItems: OrderItemResponse[];
}