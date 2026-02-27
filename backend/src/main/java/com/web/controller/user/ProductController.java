package com.web.controller.user;

import com.web.dto.request.product.ProductCreateRequest;
import com.web.dto.request.product.ProductUpdateRequest;
import com.web.dto.request.reviews.ReviewRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.security.SecurityUtil;
import com.web.service.IProductService;
import com.web.service.IReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final IProductService productService;
    private final IReviewService reviceService;

    @GetMapping
    public ApiResponse<?> getProducts() {
        return ApiResponse.success(productService.getProductsForPreview());
    }


    @GetMapping("slug/{slug}")
    public ApiResponse<?> getProductBySlug(@PathVariable String slug){
        return ApiResponse.success(productService.getProductBySlug(slug));
    }
    @PutMapping("slug/{slug}")
    public void updateViewProductBySlug(@PathVariable String slug){
        productService.updateViewProductBySlug(slug);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }
    @GetMapping("/category/{categoryId}")
    public ApiResponse<?> getProductByCategoryId(@PathVariable Long categoryId){
        return ApiResponse.success(productService.getProductByCategory(categoryId));
    }
    
    
    
    @GetMapping("/reviews/{productId}/list")
    public ApiResponse<?> getReviewsByProductId(@PathVariable Long productId){
        return ApiResponse.success(productService.getReviewsByProductId(productId));
    }
    
    @PutMapping("/reviews/{productId}")
    public ApiResponse<?> updateReviewByProductId(@RequestBody ReviewRequest reviewRequest){
        return ApiResponse.success(reviceService.addReview(reviewRequest));
    }
    
    @GetMapping("/reviews/can-rate/{productId}")
    public boolean canRate(@PathVariable Long productId){
        return reviceService.canRate(productId);
    }

}
