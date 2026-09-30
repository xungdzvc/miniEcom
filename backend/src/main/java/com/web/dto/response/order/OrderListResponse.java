package com.web.dto.response.order;

import com.web.enums.OrderStatus;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderListResponse {
    private Long orderId;
    private OrderStatus status;
    private Long total;
    private Instant orderDate;
}
