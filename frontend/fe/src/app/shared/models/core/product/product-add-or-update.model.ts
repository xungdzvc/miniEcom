export interface ProductAddOrUpdate {
  id: number;
  name: string;
  price: number;
  categoryId: number;

  thumbnail?: string;
  description?: string;
  installTotoirial?: string;
  status : boolean;
  quantity: number;
  discount: number;
  
  
  downloadUrl?: string;
  youtubeUrl?: string;
  demoUrl?: string;
  imageUrls?: string[];
  
}
