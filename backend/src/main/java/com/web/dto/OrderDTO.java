package com.web.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Data
public class OrderDTO {
    private Long Id;
    private Long userId;
    private LocalDateTime orderDate;
    private List<OrderItemDTO> orderItems;
    private String status;
    private float totalPrice;

}
