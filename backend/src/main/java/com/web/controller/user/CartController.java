package com.web.controller.user;

import com.web.dto.request.cart.CartItemQuantityRequest;
import com.web.dto.request.cart.CartItemRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.service.ICartService;
import com.web.service.ICouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICouponService couponService;
    private final ICartService cartService;

    @PostMapping("/items")
    public ApiResponse<?> addProductToCart(@Valid @RequestBody CartItemRequest cartItemRequest) {
        return ApiResponse.success(cartService.addProductToCart(cartItemRequest.getSlug()),"Thêm sản phẩm vào giỏ hàng thành công");
    }

    @PostMapping("/coupon/{code}")
    public ApiResponse<?> couponDiscount(@PathVariable String code) {
        return ApiResponse.success(couponService.getCouponDiscount(code),"Áp dụng mã giảm giá thành công");
    }

    @DeleteMapping("/{cartItemId}")
    public ApiResponse<?> removeProductFromCart(@PathVariable Long cartItemId) {
        return ApiResponse.success(cartService.removeProductFromCart(cartItemId),"Xóa sản phẩm khỏi giỏ hàng thành công");
    }

    @DeleteMapping("/items")
    public ApiResponse<?> cleanCart() {
        return ApiResponse.success(cartService.clearCart(),"Xóa giỏ hàng thành công");
    }

    @PutMapping("/items/{cartItemId}")
    public ApiResponse<?> updateProductQuantityFromCart(@PathVariable Long cartItemId,
            @Valid @RequestBody CartItemQuantityRequest cartItemQuantityRequest) {
        return ApiResponse.success(cartService.updateProductQuantityFromCart(cartItemId, cartItemQuantityRequest.getQuantity()),"Cập nhật số lượng sản phẩm trong giỏ hàng thành công");
    }

    @GetMapping()
    public ApiResponse<?> getCart() {
        return ApiResponse.success(cartService.getCart(),"Lấy giỏ hàng thành công");
    }

}
