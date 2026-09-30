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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @GetMapping
    public ApiResponse<?> getProducts(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<?> result = productService.getProductsForPreview(pageable);
        return ApiResponse.success(result, "Lấy dữ liệu sản phẩm thành công");
    }

    @GetMapping("/{slug}")
    public ApiResponse<?> getProductBySlug(@PathVariable String slug) {
        return ApiResponse.success(productService.getProductBySlug(slug));
    }

    @PatchMapping("/{slug}")
    public ApiResponse<?> updateViewProductBySlug(@PathVariable String slug) {
        productService.updateViewProductBySlug(slug);
        return ApiResponse.success("cập nhật lượt xem thành công");
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<?> getProductByCategoryId(@PathVariable Long categoryId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<?> result = productService.getProductByCategory(categoryId, pageable);
        return ApiResponse.success(result, "Lấy thành công sản phẩm theo danh mục");
    }
}
