export interface CouponAdminRequest {
  couponCode: string;
  discount: number;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface CouponAdminResponse {
  id: number;
  couponCode: string;
  discount: number;
  createdAt: string | null;
  updatedAt: string | null;
}