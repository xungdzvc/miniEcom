package com.web.mapper;

import com.web.dto.request.product.ProductCreateOrUpdateRequest;
import com.web.dto.request.product.ProductCreateRequest;
import com.web.dto.request.product.ProductDetailDTO;
import com.web.dto.request.product.ProductUpdateRequest;
import com.web.dto.response.product.ProductAdminListResponse;
import com.web.dto.response.product.ProductDetailResponse;
import com.web.dto.response.product.ProductImageDTO;
import com.web.dto.response.product.ProductResponse;
import com.web.dto.response.product.ProductViewerListResponse;
import com.web.elastic.document.ProductDocument;
import com.web.entity.ProductDetailEntity;
import com.web.entity.ProductEntity;
import com.web.entity.ProductImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    /* ===================== CREATE ===================== */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "productDetail", ignore = true)
    @Mapping(target = "productImages", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    ProductEntity toEntity(ProductCreateOrUpdateRequest request);

    @Mapping(source = "productDetail.viewCount", target = "viewCount")
    @Mapping(source = "productDetail.saleCount", target = "saleCount")
    @Mapping(source = "productDetail.discount", target = "discount")
    @Mapping(source = "productDetail.youtubeUrl", target = "youtubeUrl")
    @Mapping(source = "productDetail.demoUrl", target = "demoUrl")
    @Mapping(source = "productDetail.technology", target = "technology")
    @Mapping(source = "productDetail.installTutorial", target = "installTutorial")
    @Mapping(source = "productDetail.quantity", target = "quantity")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "imageUrls",
            expression = "java(this.mapImageUrls(entity))")
    ProductResponse toResponse(ProductEntity entity);

    /* ===================== RESPONSE ===================== */
    @Mapping(source = "productDetail.discount", target = "discount")
    @Mapping(source = "productDetail.quantity", target = "quantity")
    @Mapping(source = "category.name", target = "categoryName")
    ProductAdminListResponse toProductAdminListResponse(ProductEntity entity);

    @Mapping(source = "productDetail.viewCount", target = "viewCount")
    @Mapping(source = "productDetail.saleCount", target = "saleCount")
    @Mapping(source = "productDetail.discount", target = "discount")
    @Mapping(source = "category.name", target = "categoryName")
    ProductViewerListResponse toProductViewerListResponse(ProductEntity entity);

    @Mapping(target = "product", ignore = true)
    ProductDetailEntity toDetailEntity(ProductDetailDTO dto);

    @Mapping(target = "productId", source = "product.id")
    ProductDetailDTO toDetailDTO(ProductDetailEntity entity);

    @Mapping(target = "productId", source = "product.id")
    ProductImageDTO toProductImageDTO(ProductImageEntity entity);

    @Mapping(target = "product.id", source = "productId")
    ProductImageEntity toProductImageEntity(ProductImageDTO dto);

    @Mapping(target = "categoryName", source = "category.name")
    ProductDocument toProductDocument(ProductEntity entity);

    /* ===================== HELPERS ===================== */
    default List<String> mapImageUrls(ProductEntity entity) {
        if (entity.getProductImages() == null) {
            return List.of();
        }
        return entity.getProductImages()
                .stream()
                .map(ProductImageEntity::getImageUrl)
                .toList();
    }

}
