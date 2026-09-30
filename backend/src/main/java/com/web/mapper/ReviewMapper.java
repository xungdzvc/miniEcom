package com.web.mapper;
 
import com.web.dto.response.reviews.ReviewResponse; 
import com.web.entity.ReviewEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper { 

    @Mapping(target = "productId", source = "product.id") 
    @Mapping(target = "id", source = "id") 
    @Mapping(target = "createdAt", source = "createdAt") 
    @Mapping(target = "fullName", source = "user.fullName") 
    @Mapping(target = "username", source = "user.username") 
    @Mapping(target = "userAvatar", ignore = true) 
    ReviewResponse toResponse(ReviewEntity review);

}

