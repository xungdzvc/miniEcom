package com.web.dto.response.order;

import com.web.dto.OrderItemDTO;
import com.web.dto.response.cart.CartItemResponse;
import com.web.enums.PaymentMethod;
import java.math.BigDecimal;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderCheckoutResponse {
    private PaymentMethod paymentMethod;
    private Long orderId;
    private String status;
    private Long total;
    private LocalDateTime orderDate;
    private LocalDateTime expiresAt;
    private String QRCodeUrl;
}
