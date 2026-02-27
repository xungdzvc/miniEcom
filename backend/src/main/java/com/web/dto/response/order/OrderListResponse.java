package com.web.dto.response.order;

import com.web.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderListResponse {
    private Long orderId;
    private OrderStatus status;
    private Long total;
    private LocalDateTime orderDate;
}
