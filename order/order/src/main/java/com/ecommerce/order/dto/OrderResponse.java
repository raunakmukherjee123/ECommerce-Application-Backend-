package com.ecommerce.order.dto;


import com.ecommerce.order.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private List<OrderItemDto> orderItemDtos;
    private LocalDateTime createdAt;

}
