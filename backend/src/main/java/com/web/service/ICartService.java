package com.web.service;

import com.web.dto.CartDTO;
import com.web.dto.CartItemDTO;
import com.web.dto.response.cart.CartResponse;
import com.web.entity.CartItemEntity;
import com.web.entity.CartEntity;

public interface ICartService{
    CartResponse addProductToCart(String slug);
    CartResponse removeProductFromCart(Long cartItemId);
    CartResponse updateProductQuantityFromCart(Long cartItemId,Integer quantity);
    CartResponse getCart();
    CartResponse clearCart();
}
