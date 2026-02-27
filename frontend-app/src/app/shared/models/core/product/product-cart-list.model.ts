export interface ProductCartListDetail {
  id: number;
  productId:number;
  productName: string;
  price: number;
  categoryName: string;
  thumbnail?: string;
  discount?: number;
  quantity?:number;
  slug: string;
}
