package com.web.service;

import com.web.dto.request.product.ProductCreateOrUpdateRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.dto.response.product.ProductAdminListResponse;
import com.web.dto.response.product.ProductResponse;
import com.web.dto.response.product.ProductViewerListResponse;
import com.web.dto.response.reviews.ReviewResponse;

import java.util.List;

public interface IProductService {
    ProductResponse addOrUpdateProduct(ProductCreateOrUpdateRequest productDTO, Long id);
    ApiResponse deleteProduct(Long id);
    ApiResponse changeStatusProduct(Long id,boolean status);
    void changePinStatusProduct(Long id,boolean status);
    List<ProductAdminListResponse> getProductsForAdmin();
    List<ProductViewerListResponse> getProductsForPreview();
    ProductResponse getProduct(Long id);
    ProductResponse getProductBySlug(String slug);
    void updateViewProductBySlug(String slug);
    int getCountTotal();
    int getCountProductActive();
    int getCountProductInActive();
    List<ProductViewerListResponse> getProductByCategory(Long categoryId);
    List<ReviewResponse> getReviewsByProductId(Long productId);
    


}
