package com.web.dto.response.order;
 
import com.web.enums.PaymentMethod; 

import java.time.LocalDateTime; 
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCheckoutResponse {
    private PaymentMethod paymentMethod;
    private Long orderId;
    private String status;
    private Long total;
    private String transferContent;
    private LocalDateTime orderDate;
    private LocalDateTime expiresAt;
    private String QRCodeUrl;
}
