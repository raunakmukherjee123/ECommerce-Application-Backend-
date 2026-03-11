package com.ecommerce.order.client;

import com.ecommerce.order.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface UserServiceClient {

    @GetExchange("/api/users/{id}")
    public UserResponse getUser(@PathVariable Long id);
}
