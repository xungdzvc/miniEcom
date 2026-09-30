package com.web.dto.response.order;
 
import com.web.enums.PaymentMethod; 
import java.math.BigDecimal;

import java.time.Instant; 
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCheckoutResponse {
    private PaymentMethod paymentMethod;
    private Long orderId;
    private String status;
    private BigDecimal total;
    private String transferContent;
    private Instant orderDate;
    private Instant expiresAt;
    private String QRCodeUrl;
}
