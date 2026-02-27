package com.web.controller.user;

import com.web.dto.request.cart.CartItemQuantityRequest;
import com.web.dto.request.cart.CartItemRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.security.SecurityUtil;
import com.web.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cart")
public class CartController {

    @Autowired
    ICartService iCartService;

    @PostMapping("/add")
    public ResponseEntity<?> addProductToCart(@RequestBody CartItemRequest cartItemRequest) {
        return ResponseEntity.ok(iCartService.addProductToCart(cartItemRequest.getSlug()));
    }

    @PostMapping("/coupon/{code}")
    public ApiResponse<?> couponDiscount(@PathVariable String code) {
        return ApiResponse.success(iCartService.getCouponDiscount(code));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<?> removeProductFromCart(@PathVariable Long cartItemId) {
        return ResponseEntity.ok(iCartService.removeProductFromCart(cartItemId));
    }
    @DeleteMapping("/clear")
    public ResponseEntity<?> cleanCart() {
        return ResponseEntity.ok(iCartService.clearCart());
    }

    @PutMapping("/update-qty")
    public ResponseEntity<?> updateProductQuantityFromCart(@RequestBody CartItemQuantityRequest cartItemQuantityRequest) {
        return ResponseEntity.ok(iCartService.updateProductQuantityFromCart(cartItemQuantityRequest.getCartItemId(), cartItemQuantityRequest.getQuantity()));
    }

    @GetMapping()
    public ApiResponse<?> getCart() {
        return ApiResponse.success(iCartService.getCart());
    }

}
