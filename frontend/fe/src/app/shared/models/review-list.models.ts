export interface ReviewResponse {
  id: number;
  productId: number;
  fullName: string;
  userName: string;
  rate: number;
  comment?: string;
  userAvatar?: string;
  createdAt: string;
}
