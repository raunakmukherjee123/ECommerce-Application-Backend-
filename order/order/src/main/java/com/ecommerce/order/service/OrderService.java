package com.ecommerce.order.service;


import com.ecommerce.order.dto.OrderCreatedEvent;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.enums.OrderStatus;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public Optional<OrderResponse> createOrder(String userId) {
        List<CartItem> cartItems=cartService.getCarts(userId);

        if(cartItems.isEmpty())
        {
            return Optional.empty();
        }

//        Optional<User> optionalUser=userRepository.findById(Long.valueOf(userId));
//
//        if(optionalUser.isEmpty())
//        {
//            return Optional.empty();
//        }
//
//        User user=optionalUser.get();

        BigDecimal totalPrice=cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        Order order=new Order();

        order.setUserId(Long.valueOf(userId));
        order.setTotalAmount(totalPrice);
        order.setStatus(OrderStatus.CONFIRMED);

        List<OrderItem> orderItems=cartItems.stream()
                .map(cartItem -> new OrderItem(null,
                        cartItem.getProductId(),
                        cartItem.getQuantity(),
                        cartItem.getPrice(),
                        order
                ))
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        Order savedOrder=orderRepository.save(order);

        cartService.clearCart(userId);

        // publish order created event
        OrderCreatedEvent orderCreatedEvent=new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getStatus(),
                savedOrder.getTotalAmount(),
                mapToListOrderItemDto(savedOrder.getOrderItems()),
                savedOrder.getCreatedAt()
                );

//        rabbitTemplate.convertAndSend("order.exchange","order.tracking",
//                Map.of("orderId",savedOrder.getId(),"status","CREATED"));

        rabbitTemplate.convertAndSend("order.exchange","order.tracking",
                orderCreatedEvent);


        return Optional.of(mapToOrderResponse(savedOrder));
    }

    private List<OrderItemDto> mapToListOrderItemDto(List<OrderItem> orderItems)
    {
      return orderItems.stream()
              .map(orderItem -> new OrderItemDto(
                      orderItem.getId(),
                      orderItem.getProductId(),
                      orderItem.getQuantity(),
                      orderItem.getPrice(),
                      orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))))
              .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order savedOrder) {
        OrderResponse orderResponse=new OrderResponse();

        orderResponse.setOrderStatus(savedOrder.getStatus());
        List<OrderItem> orderItems=savedOrder.getOrderItems();

        List<OrderItemDto> orderItemDtos=orderItems.stream()
                .map(orderItem -> mapToOrderItemDTO(orderItem))
                .collect(Collectors.toList());

        orderResponse.setOrderItemDtos(orderItemDtos);
        orderResponse.setCreatedAt(savedOrder.getCreatedAt());
        orderResponse.setTotalAmount(savedOrder.getTotalAmount());

        return orderResponse;
    }

    private OrderItemDto mapToOrderItemDTO(OrderItem orderItem) {
        OrderItemDto orderItemDto=new OrderItemDto();

        orderItemDto.setId(orderItem.getId());
        orderItemDto.setProductId(orderItem.getProductId());
        orderItemDto.setPrice(orderItem.getPrice());
        orderItemDto.setQuantity(orderItem.getQuantity());
        orderItemDto.setSubTotal(orderItem.getPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity())));

        return orderItemDto;
    }
}
