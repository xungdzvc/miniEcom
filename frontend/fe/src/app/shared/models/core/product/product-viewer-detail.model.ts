export interface ProductViewerDetail {
    id: number;
    name: string;
    price: number;
    categoryId : number;
    categoryName: string;
    thumbnail?: string;
    description?: string;
    viewCount?: number;
    saleCount?: number;
    discount : number;
    youtubeUrl?: string;
    demoUrl?: string;
    imageUrls?: string[];
    slug : string;
    technology?: string;
    installTutorial?: string;
    shareBy?: string;
}