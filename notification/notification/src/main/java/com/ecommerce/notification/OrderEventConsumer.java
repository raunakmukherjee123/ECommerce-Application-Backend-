package com.ecommerce.notification;

import com.ecommerce.notification.payload.OrderCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderEventConsumer {

    @RabbitListener(queues = "${rabbitmq.queue.name}")
//    public void orderHandleEvent(Map<String,Object> orderEvent)
    public void orderHandleEvent(OrderCreatedEvent orderCreatedEvent)
    {
        System.out.println("Received order event: "+orderCreatedEvent);

        Long orderId=orderCreatedEvent.getOrderId();
        String status= String.valueOf(orderCreatedEvent.getOrderStatus());

        System.out.println("Order id is "+ orderId);
        System.out.println("Status is "+ status);
    }
}

// once a message has been consumed from queue, it will be removed from queue
