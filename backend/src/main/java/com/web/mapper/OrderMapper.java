package com.web.mapper;

import com.web.dto.OrderItemDTO;
import com.web.dto.response.order.OrderCheckoutResponse;
import com.web.dto.response.order.OrderItemResponse;
import com.web.dto.response.order.OrderDetailResponse;
import com.web.dto.response.order.OrderListResponse;
import com.web.entity.OrderEntity;
import com.web.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "QRCodeUrl", ignore = true)
    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "transferContent", ignore = true)
    OrderCheckoutResponse toOrderCheckoutResponse(OrderEntity orderEntity);

    @Mapping(target = "orderId", source = "id")
    OrderDetailResponse toOrderDetailResponse(OrderEntity orderEntity);

    @Mapping(target = "orderId", source = "id")
    OrderListResponse toOrderResponse(OrderEntity orderEntity);

    @Mapping(target = "id", source = "orderId")
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    OrderEntity CheckOutResponsetoOrderEntity(OrderCheckoutResponse orderCheckoutResponse);

    @Mapping(target = "orderItemId", source = "id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productPrice", source = "price")
    @Mapping(target = "thumbnail", source = "product.thumbnail")
    @Mapping(target = "categoryName", source = "product.category.name")
    OrderItemResponse toOrderItemDTO(OrderItemEntity orderItemEntity);

    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "product.id", source = "productId")
    OrderItemEntity toOrderItemEntity(OrderItemDTO orderItemDTO);
}
