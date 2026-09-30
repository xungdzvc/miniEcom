package com.web.service;

import com.web.dto.request.product.ProductCreateOrUpdateRequest; 
import com.web.dto.response.product.ProductAdminListResponse;
import com.web.dto.response.product.ProductResponse;
import com.web.dto.response.product.ProductViewerListResponse;
import com.web.dto.response.reviews.ReviewResponse;
import com.web.dto.response.product.ProductDetailResponse;
import com.web.entity.ProductEntity;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {
    ProductResponse addOrUpdateProduct(ProductCreateOrUpdateRequest productDTO, Long id);
    
    ProductResponse createProduct(ProductCreateOrUpdateRequest productDTO);
    ProductResponse updateProduct(ProductCreateOrUpdateRequest productDTO, Long id);
    void deleteProduct(Long id);
    void changeStatusProduct(Long id,boolean status);
    void changePinStatusProduct(Long id,boolean status);
    Page<ProductAdminListResponse> getProductsForAdmin(Pageable page);
    Page<ProductViewerListResponse> getProductsForPreview(Pageable page);
    ProductResponse getProductForAdmin(Long id);
    ProductDetailResponse getProduct(Long id);
    ProductDetailResponse getProductBySlug(String slug);
    void updateViewProductBySlug(String slug);
    int getCountTotal();
    int getCountProductActive();
    int getCountProductInActive();
    Page<ProductViewerListResponse> getProductByCategory(Long categoryId,Pageable page);
    List<ReviewResponse> getReviewsByProductId(Long productId);
    void incrementSalesCount(ProductEntity product, Integer soluong);
    


}
