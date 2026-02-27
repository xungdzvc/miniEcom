package com.web.dto.response.order;

import com.web.dto.OrderDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailResponse {

    private long orderId;
    private String status;
    private Long total;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> orderItems;

}
