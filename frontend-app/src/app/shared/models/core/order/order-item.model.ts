export interface OrderItemResponse {
    orderItemId: number;
    productId: number;
    productName: string;
    categoryName: string;
    thumbnail?: string;
    productPrice: number;
    quantity: number;
}