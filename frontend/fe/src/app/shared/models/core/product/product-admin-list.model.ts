export interface ProductAdminList {
  id: number;
  name: string;
  price: number;
  categoryName: string;
  thumbnail?: string;
  quantity : number;
  status : boolean;
  createdAt: Date;
  updatedAt: Date;
  viewCount?: number;
  saleCount?: number;
  pin : boolean;
}
