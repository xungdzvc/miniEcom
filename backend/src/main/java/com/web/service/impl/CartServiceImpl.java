package com.web.service.impl;

import com.web.dto.CartDTO;
import com.web.dto.CartItemDTO;
import com.web.dto.response.cart.CartItemResponse;
import com.web.dto.response.cart.CartResponse;
import com.web.entity.CartItemEntity;
import com.web.entity.CartEntity;
import com.web.entity.ProductEntity;
import com.web.entity.UserEntity;
import com.web.exception.MyException;
import com.web.mapper.CartMapper;
import com.web.repository.*;
import com.web.security.SecurityUtil;
import com.web.service.ICartService;
import com.web.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartMapper cartMapper;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartItemRepository CartItemRepository;

    @Override
    public CartResponse addProductToCart(String slug) {
        Long userId = SecurityUtil.getUserId();
        CartEntity cartEntity = cartRepository.findByUserId(userId);
        if(cartEntity == null){
            UserEntity user = userRepository.getReferenceById(userId);
            cartEntity = new CartEntity();
            cartEntity.setUser(user);

        }
        ProductEntity productEntity = productRepository.findBySlug(slug);
        if (productEntity == null) {
            throw new MyException("Sản phẩm không tồn tại");
        }
        CartItemEntity existProduct = CartItemRepository.findByCartIdAndProductId(cartEntity.getId(), productEntity.getId());

        if (existProduct != null) {
            existProduct.setQuantity(existProduct.getQuantity() + 1);
        } else {
            CartItemEntity CartItemEntity = new CartItemEntity();
            CartItemEntity.setCart(cartEntity);
            CartItemEntity.setProduct(productEntity);
            CartItemEntity.setQuantity(1);
            cartEntity.getCartItems().add(CartItemEntity);

        }
        cartRepository.save(cartEntity);
        CartResponse cartResponse = cartMapper.toCartResponse(cartEntity);
        long toltalPriceProduct = 0;
        for (CartItemResponse cart : cartResponse.getCartItems()) {
            toltalPriceProduct += Utils.calsubPercent(cart.getPrice(), cart.getDiscount());
        }
        cartResponse.setToltalPrice(toltalPriceProduct);
        return cartResponse;
    }

    @Override
    public CartResponse removeProductFromCart(Long cartItemId) {
        Long userId = SecurityUtil.getUserId();
        CartEntity cartEntity = cartRepository.findByUserId(userId);
        if(cartEntity == null){
            throw new MyException("Giỏ hàng không tồn tại");
        }
       
        CartItemEntity existProduct = cartEntity.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst().orElse(null);

        if (existProduct == null) {
            throw new MyException("Sản phẩm không tồn tại hoặc đã được xoá");
        }
        {
            cartEntity.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        }
        cartRepository.save(cartEntity);
        CartResponse cartResponse = cartMapper.toCartResponse(cartEntity);
        float toltalPriceProduct = 0;
        for (CartItemResponse cart : cartResponse.getCartItems()) {
            toltalPriceProduct += Utils.calsubPercent(cart.getPrice(), cart.getDiscount());
        }
        cartResponse.setToltalPrice(toltalPriceProduct);
        return cartResponse;

    }

    @Override
    public CartResponse getCart() {
        Long userId = SecurityUtil.getUserId();
        CartEntity cartEntity = cartRepository.findByUserId(userId);
        CartResponse cartResponse = cartMapper.toCartResponse(cartEntity);
        float toltalPriceProduct = 0;
        for (CartItemResponse cart : cartResponse.getCartItems()) {
            toltalPriceProduct += Utils.calsubPercent(cart.getPrice(), cart.getDiscount());
        }
        cartResponse.setToltalPrice(toltalPriceProduct);
        return cartResponse;
    }

    

    @Transactional
    @Override
    public CartResponse updateProductQuantityFromCart(Long cartItemId, Integer quantity) {
        Long userId = SecurityUtil.getUserId();
        if (quantity == null || quantity < 1) {
            throw new MyException("Số lượng phải >= 1");
        }

        int updated = cartItemRepository.updateQty(userId, cartItemId, quantity);
        if (updated == 0) {
            throw new MyException("Không tìm thấy sản phẩm trong giỏ");
        }

        CartEntity cart = cartRepository.findByUserId(userId);

        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse clearCart() {
        Long userId = SecurityUtil.getUserId();
        CartEntity cart = cartRepository.findByUserId(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
        return cartMapper.toCartResponse(cart);
    }

}
