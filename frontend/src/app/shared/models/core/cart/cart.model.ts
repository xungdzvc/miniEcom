import { ProductCartListDetail } from "../product/product-cart-list.model";
export interface CartResponse{
    id:number,
    userId:number,
    toltalPrice:number,
    cartItems: ProductCartListDetail[];
}