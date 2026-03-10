export interface Review {
    id: string; 
    productId: number;
    userId: number;
    rating: number; // 1 to 5
    comment: string;
    createdAt: string; // ISO date string
    userName?: string; // optional, name of the user who made the review
}