package com.web.service.impl;

import com.web.dto.response.cart.CartResponse;
import com.web.entity.CartItemEntity;
import com.web.entity.CartEntity;
import com.web.entity.ProductDetailEntity;
import com.web.entity.ProductEntity;
import com.web.entity.UserEntity;
import com.web.exception.MyException;
import com.web.mapper.CartMapper;
import com.web.repository.*;
import com.web.security.SecurityUtil;
import com.web.service.ICartService;
import com.web.util.Utils;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartMapper cartMapper;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public CartResponse addProductToCart(String slug) {
        Long userId = SecurityUtil.getUserId();
        if(userId == null){
            throw new MyException("Người dùng chưa đăng nhập hoặc lỗi tài khoản");
        }
        CartEntity cartEntity = cartRepository.findByUserId(userId);
        if (cartEntity == null) {
            UserEntity user = userRepository.getReferenceById(userId);
            cartEntity = new CartEntity();
            cartEntity.setUser(user);
            cartRepository.saveAndFlush(cartEntity);

        }
        ProductEntity productEntity = productRepository.findBySlug(slug);
        if (productEntity == null) {
            throw new MyException("Sản phẩm không tồn tại");
        }
        CartItemEntity existProduct = cartItemRepository.findByCartIdAndProductId(cartEntity.getId(), productEntity.getId());

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
        cartResponse.setToltalPrice(calculateTotal(cartEntity.getCartItems()));
        return cartResponse;
    }

    @Transactional
    @Override
    public CartResponse removeProductFromCart(Long cartItemId) {
        CartEntity cartEntity = cartRepository.findByIdAndUserId(cartItemId,SecurityUtil.getUserId())
                .orElseThrow(() -> new MyException("Giỏ hàng không tồn tại"));

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
        cartResponse.setToltalPrice(calculateTotal(cartEntity.getCartItems()));
        return cartResponse;

    }

    @Override
    public CartResponse getCart() {
        Long userId = SecurityUtil.getUserId();
        CartEntity cartEntity = cartRepository.findByUserId(userId);
        CartResponse cartResponse = cartMapper.toCartResponse(cartEntity);
        cartResponse.setToltalPrice(calculateTotal(cartEntity.getCartItems()));
        return cartResponse;
    }

    @Transactional
    @Override
    public CartResponse updateProductQuantityFromCart(Long cartItemId, Integer quantity) {
        CartItemEntity cartI = cartItemRepository.findById(cartItemId).orElseThrow(() -> new MyException("Không tồn tại item này"));
        validationProductForUpdateCart(cartI, quantity);
        Long userId = SecurityUtil.getUserId();

        int updated = cartItemRepository.updateQty(userId, cartItemId, quantity);
        if (updated == 0) {
            throw new MyException("Không tìm thấy sản phẩm trong giỏ");
        }

        return cartMapper.toCartResponse(cartI.getCart());
    }

    private void validationProductForUpdateCart(CartItemEntity cartI, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new MyException("Số lượng phải >= 1");
        }
        if (cartI.getProduct().getProductDetail().getQuantity() < quantity) {
            throw new MyException(cartI.getProduct().getName() + " Chỉ còn " + cartI.getProduct().getProductDetail().getQuantity() + " trong kho hãy giảm số lượng xuống hoặc chọn mặt hàng khác thay thế");
        }
    }

    @Transactional
    @Override
    public CartResponse clearCart() {
        Long userId = SecurityUtil.getUserId();
        CartEntity cart = cartRepository.findByUserId(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
        return cartMapper.toCartResponse(cart);
    }

    private BigDecimal calculateTotal(List<CartItemEntity> items) {
        return items.stream()
                .map(item -> {
                    BigDecimal price = item.getProduct().getPrice();
                    int discount = Optional.ofNullable(item.getProduct().getProductDetail())
                            .map(ProductDetailEntity::getDiscount)
                            .orElse(0);
                    return Utils.calsubPercent(price, discount).multiply(new BigDecimal(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
