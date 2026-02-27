export interface ProductViewerListDetail {
  id: number;
  name: string;
  price: number;
  description: string;
  categoryName: string;
  thumbnail?: string;
  viewCount?: number;
  saleCount?: number;
  discount?: number;
  slug : string;
}
