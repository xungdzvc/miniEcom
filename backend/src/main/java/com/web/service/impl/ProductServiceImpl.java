package com.web.service.impl;

import com.web.dto.request.product.ProductCreateOrUpdateRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.dto.response.product.ProductAdminListResponse;
import com.web.dto.response.product.ProductResponse;
import com.web.dto.response.product.ProductViewerListResponse;
import com.web.dto.response.reviews.ReviewResponse;
import com.web.elastic.document.ProductDocument;
import com.web.entity.CategoryEntity;
import com.web.entity.ProductDetailEntity;
import com.web.entity.ProductEntity;
import com.web.entity.ProductImageEntity;
import com.web.entity.ReviewEntity;
import com.web.entity.UserEntity;
import com.web.exception.MyException;
import com.web.mapper.ProductMapper;
import com.web.repository.*;
import com.web.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import com.web.security.SecurityUtil;
import com.web.service.IStorageService;
import com.web.service.elastic.ProductElasticService;
import com.web.util.Utils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final ReviewRepository reviewRepository;
    private final IStorageService storageService;
    private final ProductElasticService productElasticService;

    @Override
    public ProductResponse addOrUpdateProduct(ProductCreateOrUpdateRequest productDTO, Long productId) {
        Long userId = SecurityUtil.getUserId();
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new MyException("Người bán không tồn tại"));
        ProductEntity product = new ProductEntity();
        LocalDateTime now = LocalDateTime.now();
        if (productId == null) {
            product = productMapper.toEntity(productDTO);
            product.setCreatedAt(now);
            product.setUser(userEntity);

        } else {

            product = productRepository.findById(productId).orElseThrow(() -> new MyException("Sản phẩm lỗi"));
            if (!SecurityUtil.isAdmin() && !userId.equals(product.getUser().getId())) {
                throw new MyException("bạn không đủ quyền để thực hiện thao tác này");
            }

        }
        product.setUpdatedAt(now);

        CategoryEntity categoryEntity = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(() -> new MyException("Danh mục không tồn tại"));

        product.setStatus(productDTO.getStatus());
        product.setThumbnail(productDTO.getThumbnail());
        product.setSlug(Utils.slugify(productDTO.getName()));
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setCategory(categoryEntity);
        product.setPrice(productDTO.getPrice());
        ProductDetailEntity productDetail;

        if (product.getProductDetail() != null) {
            productDetail = product.getProductDetail();
        } else {
            productDetail = new ProductDetailEntity();
        }

        productDetail.setDemoUrl(productDTO.getDemoUrl());
        productDetail.setDownloadUrl(productDTO.getDownloadUrl());
        productDetail.setYoutubeUrl(productDTO.getYoutubeUrl());
        productDetail.setDiscount(productDTO.getDiscount());
        productDetail.setQuantity(productDTO.getQuantity());
        productDetail.setInstallTutorial(productDTO.getInstallTutorial());
        productDetail.setTechnology(productDTO.getTechnology());
        productDetail.setPin(productDTO.getPin());
        productDetail.setShareBy(productDTO.getShareBy());
        productDetail.setProduct(product);
        product.setProductDetail(productDetail);
        replaceImage(productDTO.getImageUrls(), product);

        productRepository.save(product);
        productElasticService.updateProduct(product);
        return productMapper.toResponse(product);
    }

    @Override
    public ApiResponse<?> changeStatusProduct(Long id, boolean status) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(() -> new MyException("Sản phẩm không tồn tại"));
        productEntity.setStatus(status);
        productRepository.save(productEntity);
        return ApiResponse.success(null, "Cập nhật thành công");

    }

    @Override
    public ApiResponse<?> deleteProduct(Long id) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(() -> new MyException("Sản phẩm không tồn tại"));

        List<String> LImagesUrl = new ArrayList<>();
        LImagesUrl.add(productEntity.getThumbnail());
        for (ProductImageEntity e : productEntity.getProductImages()) {
            LImagesUrl.add(e.getImageUrl());
        }

        productRepository.delete(productEntity);
        for (String url : LImagesUrl) {
            storageService.delete(url);
        }
        productElasticService.deleteProduct(id);

        return ApiResponse.success(null, "Xoá thành công ");

    }

    @Override
    public List<ProductAdminListResponse> getProductsForAdmin() {

        List<ProductEntity> productEntities = new ArrayList<>();
        List<ProductAdminListResponse> productAdminResponse = new ArrayList<>();
        if (SecurityUtil.isAdmin()) {
            productEntities = productRepository.findAll();
        } else if (SecurityUtil.isStaff()) {
            productEntities = productRepository.findByUser_Id(SecurityUtil.getUserId());
        }
        productAdminResponse = productEntities.stream().map(productMapper::toProductAdminListResponse)
                .toList();
        return productAdminResponse;
    }

    @Override
    public List<ProductViewerListResponse> getProductsForPreview() {
        List<ProductEntity> productEntities = productRepository.findByStatusTrue();
        return productEntities.stream().map(productMapper::toProductViewerListResponse)
                .toList();
    }

    @Override
    public ProductResponse getProduct(Long id) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(() -> new MyException("Sản phẩm không tồn tại"));
        return productMapper.toResponse(productEntity);
    }

    @Override
    public ProductResponse getProductBySlug(String slug) {
        ProductEntity productEntity = productRepository.findBySlug(slug);
        return productMapper.toResponse(productEntity);
    }

    String toSlug(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    void replaceImage(
            List<String> urls,
            ProductEntity productEntity
    ) {
        if (urls == null || urls.isEmpty()) {
            return;
        }
        List<ProductImageEntity> currentImages = productEntity.getProductImages();
        if (currentImages == null) {
            currentImages = new ArrayList<>();
            productEntity.setProductImages(currentImages);
        }
        currentImages.clear();
        for (String url : urls) {
            ProductImageEntity img = new ProductImageEntity();
            img.setImageUrl(url);
            img.setProduct(productEntity);
            currentImages.add(img);
        }
    }

    @Override
    public int getCountTotal() {
        return (int) productRepository.count();
    }

    @Override
    public int getCountProductActive() {
        return (int) productRepository.countByStatusTrue();
    }

    @Override
    public int getCountProductInActive() {
        return (int) productRepository.countByStatusFalse();
    }

    @Transactional
    @Override
    public void updateViewProductBySlug(String slug) {
        ProductEntity productEntity = productRepository.findBySlug(slug);
        if (productEntity == null) {
            throw new RuntimeException("Product not found with slug: " + slug);
        }

        ProductDetailEntity productDetailEntity = productEntity.getProductDetail();
        if (productDetailEntity == null) {
            productDetailEntity = new ProductDetailEntity();
            productDetailEntity.setViewCount(0);
            productDetailEntity.setProduct(productEntity);
            productEntity.setProductDetail(productDetailEntity);
        }

        int views = productDetailEntity.getViewCount();
        productDetailEntity.setViewCount(views + 1);

        productRepository.save(productEntity);
    }

    @Override
    public List<ProductViewerListResponse> getProductByCategory(Long categoryId) {
        List<ProductEntity> productEntities = productRepository.findByCategoryId(categoryId);
        List<ProductViewerListResponse> productResponse = new ArrayList<>();
        productResponse = productEntities.stream().map(productMapper::toProductViewerListResponse)
                .toList();
        return productResponse;
    }

    @Override
    public List<ReviewResponse> getReviewsByProductId(Long productId) {
        List<ReviewEntity> reviewEntitys = reviewRepository.findByProductId(productId);
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        for (ReviewEntity review : reviewEntitys) {
            ReviewResponse reviewReponse = new ReviewResponse();
            reviewReponse.setId(review.getId());
            reviewReponse.setProductId(review.getProduct().getId());
            reviewReponse.setFullName(review.getUser().getFullName());
            reviewReponse.setUserName(review.getUser().getUsername());
            reviewReponse.setRate(review.getRate());
            reviewReponse.setComment(review.getComment());
            reviewReponse.setCreatedAt(review.getCreatedAt());
            reviewResponses.add(reviewReponse);
        }
        return reviewResponses;
    }

    @Override
    public void changePinStatusProduct(Long id, boolean status) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(() -> new MyException("Sản phẩm không tồn tại"));
        productEntity.getProductDetail().setPin(status);
        productRepository.save(productEntity);

    }

}
