package com.web.service;
 
import com.web.dto.response.cart.CartResponse; 

public interface ICartService{
    CartResponse addProductToCart(String slug);
    CartResponse removeProductFromCart(Long cartItemId);
    CartResponse updateProductQuantityFromCart(Long cartItemId,Integer quantity);
    CartResponse getCart();
    CartResponse clearCart();
}
