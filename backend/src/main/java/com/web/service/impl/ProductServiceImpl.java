package com.web.service.impl;

import com.web.dto.response.product.ProductDetailResponse;
import com.web.dto.request.product.ProductCreateOrUpdateRequest;
import com.web.dto.response.product.ProductAdminListResponse;
import com.web.dto.response.product.ProductResponse;
import com.web.dto.response.product.ProductViewerListResponse;
import com.web.dto.response.reviews.ReviewResponse;
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

import java.util.ArrayList;
import java.util.List;
import com.web.security.SecurityUtil;
import com.web.service.IStorageService;
import com.web.service.elastic.ProductElasticService;
import com.web.util.Utils;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductDetailRepository productDetailRepository;
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
        String slug = Utils.slugify(productDTO.getName());
        ProductEntity product;
        if (productId == null) {
            if (productRepository.existsBySlug(slug)) {
                throw new MyException("Sản phẩm này đã tồn tại trong cửa hàng");
            }
            product = productMapper.toEntity(productDTO);
            product.setUser(userEntity);

        } else {

            product = productRepository.findById(productId).orElseThrow(() -> new MyException("Sản phẩm lỗi"));
            if (!SecurityUtil.isAdmin()) {
                if (!userId.equals(product.getUser().getId())) {
                    throw new MyException("bạn không đủ quyền để thực hiện thao tác này");
                }
            }
            if (productRepository.existsBySlug(slug) && !slug.equals(product.getSlug())) {
                throw new MyException("Sản phẩm này đã tồn tại trong cửa hàng");
            }

        }

        CategoryEntity categoryEntity = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(() -> new MyException("Danh mục không tồn tại"));

        product.setStatus(productDTO.getStatus());
        product.setThumbnail(productDTO.getThumbnail());
        product.setSlug(slug);
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setCategory(categoryEntity);
        product.setPrice(productDTO.getPrice());
        ProductDetailEntity productDetail = new ProductDetailEntity();

        if (product.getProductDetail() != null) {
            productDetail = product.getProductDetail();
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
        Utils.replaceImage(productDTO.getImageUrls(), product);

        productRepository.save(product);
        productElasticService.updateProduct(product);
        return productMapper.toResponse(product);
    }

    @Override
    public void changeStatusProduct(Long id, boolean status) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(() -> new MyException("Sản phẩm không tồn tại"));
        productEntity.setStatus(status);
        productRepository.save(productEntity);

    }

    @Override
    public void deleteProduct(Long id) {
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

    }

    @Override //
    public Page<ProductAdminListResponse> getProductsForAdmin(Pageable page) {

        Page<ProductEntity> productEntities = null;
        if (SecurityUtil.isAdmin()) {
            productEntities = productRepository.findAll(page);
        } else if (SecurityUtil.isStaff()) {
            productEntities = productRepository.findByUser_Id(SecurityUtil.getUserId(), page);
        }
        assert productEntities != null;
        return productEntities.map(productMapper::toProductAdminListResponse);
    }

    @Override //
    public Page<ProductViewerListResponse> getProductsForPreview(Pageable page) {
        Page<ProductEntity> productEntities = productRepository.findByStatus(true, page);
        return productEntities.map(productMapper::toProductViewerListResponse);
    }

    @Override //
    public ProductDetailResponse getProduct(Long id) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(() -> new MyException("Sản phẩm không tồn tại"));
        return productMapper.toResponseDetail(productEntity);
    }

    @Override
    public ProductDetailResponse getProductBySlug(String slug) {
        ProductEntity productEntity = productRepository.findBySlug(slug);
        return productMapper.toResponseDetail(productEntity);
    }

    @Override
    public int getCountTotal() {
        return (int) productRepository.count();
    }

    @Override
    public int getCountProductActive() {
        return   productRepository.countByStatus(true);
    }

    @Override
    public int getCountProductInActive() {
        return  productRepository.countByStatus(false);
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

        int views = (productDetailEntity.getViewCount() == null) ? 0 : productDetailEntity.getViewCount();
        productDetailEntity.setViewCount(views + 1);

        productRepository.save(productEntity);
    }

    @Override
    public Page<ProductViewerListResponse> getProductByCategory(Long categoryId, Pageable page) {
        Page<ProductEntity> productEntities = productRepository.findByCategoryId(categoryId, page);
        return productEntities.map(productMapper::toProductViewerListResponse);
    }

    @Override
    public List<ReviewResponse> getReviewsByProductId(Long productId) {
        List<ReviewEntity> reviewEntities = reviewRepository.findByProductId(productId);
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        for (ReviewEntity review : reviewEntities) {
            ReviewResponse reviewResponse = new ReviewResponse();
            reviewResponse.setId(review.getId());
            reviewResponse.setProductId(review.getProduct().getId());
            reviewResponse.setFullName(review.getUser().getFullName());
            reviewResponse.setUsername(review.getUser().getUsername());
            reviewResponse.setRate(review.getRate());
            reviewResponse.setComment(review.getComment());
            reviewResponse.setCreatedAt(review.getCreatedAt());
            reviewResponses.add(reviewResponse);
        }
        return reviewResponses;
    }

    @Override
    public void changePinStatusProduct(Long id, boolean status) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(() -> new MyException("Sản phẩm không tồn tại"));
        productEntity.getProductDetail().setPin(status);
        productRepository.save(productEntity);

    }

    @Override
    public ProductResponse getProductForAdmin(Long id) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(() -> new MyException("Sản phẩm không tồn tại"));
        return productMapper.toResponse(productEntity);
    }

    @Override
    @Transactional
    public void incrementSalesCount(ProductEntity product, Integer soLuongThem) {
        productDetailRepository.incrementSalesCount(product.getProductDetail().getId(), soLuongThem);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateOrUpdateRequest product) {
        UserEntity userEntity = getUser();
        String slug = Utils.slugify(product.getName());
        if (productRepository.existsBySlug(slug)) {
            throw new MyException("Sản phẩm này đã tồn tại trong cửa hàng");
        }
        CategoryEntity categoryEntity = categoryRepository.findById(product.getCategoryId())
                .orElseThrow(() -> new MyException("Danh mục không tồn tại"));

        ProductEntity productEntity = productMapper.toEntity(product);

        productEntity.setUser(userEntity);
        productEntity.setCategory(categoryEntity);
        productEntity.setSlug(slug);

        Utils.replaceImage(product.getImageUrls(), productEntity);
        productRepository.save(productEntity);
        return productMapper.toResponse(productEntity);
    }

    @Override
    public ProductResponse updateProduct(ProductCreateOrUpdateRequest productDTO, Long id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private UserEntity getUser() {
        Long userId = SecurityUtil.getUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new MyException("Người bán không tồn tại"));
    }

}
