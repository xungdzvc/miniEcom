export interface Product {
  id: number;
  name: string;
  price: number;
  thumbnail?: string;
  categoryId: number;
  userId?: number;
  status: boolean;
  createdAt?: string;
  updatedAt?: string;
}
