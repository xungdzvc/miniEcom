package com.web.mapper;

import com.web.dto.CartDTO;
import com.web.dto.CartItemDTO;
import com.web.dto.response.cart.CartItemResponse;
import com.web.dto.response.cart.CartResponse;
import com.web.entity.CartEntity;
import com.web.entity.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(
            target = "cartItems",
            expression = "java(cartEntity.getCartItems().stream().map(this::toCartItemResponse).toList())")

    @Mapping(target = "toltalPrice", ignore = true)

    CartResponse toCartResponse(CartEntity cartEntity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "categoryName", source = "product.category.name")
    @Mapping(target = "thumbnail", source = "product.thumbnail")
    @Mapping(target = "discount", source = "product.productDetail.discount")
    @Mapping(target = "slug", source = "product.slug")
    CartItemResponse toCartItemResponse(CartItemEntity cartItemEntity);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items",  source = "cartItems")
    @Mapping(target = "totalPrice", ignore = true)
    CartDTO toCartDTO(CartEntity cartEntity);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "discount", source = "product.productDetail.discount")
    @Mapping(target = "price", source = "product.price")
    CartItemDTO toCartItem(CartItemEntity cartItemEntity);

}
