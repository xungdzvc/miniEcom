export interface ProductResponse {
  id: number;
  name: string;
  price: number;
  categoryName: string;
  thumbnail?: string;
  quantity : number;
  createdAt: Date;
  updatedAt: Date;
  viewCount?: number;
  saleCount?: number;
}
