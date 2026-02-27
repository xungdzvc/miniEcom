package com.web.service;

import com.web.dto.OrderDTO;
import com.web.dto.request.order.DirectCheckoutRequest;
import com.web.dto.request.order.OrderCheckoutRequest;
import com.web.dto.response.order.OrderCheckoutResponse;
import com.web.dto.response.order.OrderDetailResponse;
import com.web.dto.response.order.OrderListResponse;
import com.web.enums.OrderStatus;
import com.web.enums.PaymentStatus;

import java.util.List;

public interface IOrderService {
    List<OrderDetailResponse> getOrderByUserId(Long id);
    OrderCheckoutResponse updateOrder(OrderDTO orderDTO);

    OrderCheckoutResponse checkoutByBankOrWallet(OrderCheckoutRequest orderCheckoutRequest);
    OrderCheckoutResponse checkoutByDirectBankOrWallet(DirectCheckoutRequest directCheckoutRequest);
    
    List<OrderListResponse> getListOrders();
    
    OrderStatus getStatusById(Long id);

    OrderDetailResponse getOrderSuccessById(Long id);
    
    String getDownloadUrl(Long id, Long itemId);
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
}
