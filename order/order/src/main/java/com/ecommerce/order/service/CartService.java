package com.ecommerce.order.service;


import com.ecommerce.order.client.ProductServiceClient;
import com.ecommerce.order.client.UserServiceClient;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.repository.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    int attempt=0;

//    @CircuitBreaker(name = "productService", fallbackMethod = "addToCartFallback") // productService is mentioned in order.yml(config)
@Retry(name = "retryBreaker", fallbackMethod = "addToCartFallback")
    public boolean addToCart(String userId, CartItemRequest cartItemRequest) {
    System.out.println("Attempt count :"+ ++attempt);

       ProductResponse productResponse =productServiceClient.getProductById(cartItemRequest.getProductId());

        if(productResponse==null || productResponse.getStockQuantity()<cartItemRequest.getQuantity())
        {
            return false;
        }

        UserResponse userResponse=userServiceClient.getUser(Long.valueOf(userId));



        if(userResponse==null)
        {
            return false;
        }
//
//        User user=userOptional.get();

        CartItem existingCartItem= cartItemRepository.findByUserIdAndProductId(Long.valueOf(userId),cartItemRequest.getProductId());

        if(existingCartItem!=null)
        {
            existingCartItem.setQuantity(existingCartItem.getQuantity()+cartItemRequest.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(1000.0));

            cartItemRepository.save(existingCartItem);
        }
        else
        {
            CartItem cartItem=new CartItem();

            cartItem.setPrice(BigDecimal.valueOf(1000.0));
            cartItem.setUserId(Long.valueOf(userId));
            cartItem.setProductId(cartItemRequest.getProductId());
            cartItem.setQuantity(cartItemRequest.getQuantity());

            cartItemRepository.save(cartItem);
        }
         return true;
    }

    public boolean deleteItemFromCart(String userId, Long productId) {
        CartItem cartItem=cartItemRepository.findByUserIdAndProductId(Long.valueOf(userId),productId);

        if(cartItem!=null)
        {
            cartItemRepository.delete(cartItem);
            return true;
        }

        return false;
    }

    public List<CartItem> getCarts(String userId) {
//       return userRepository.findById(Long.valueOf(userId))
//               .map(user -> cartItemRepository.findByUserId(user.getId()))
//               .orElseGet(List::of);

        return cartItemRepository.findByUserId(Long.valueOf(userId));
    }

    public void clearCart(String userId) {
//        userRepository.findById(Long.valueOf(userId)).ifPresent(
//                user -> cartItemRepository.deleteByUserId(Long.valueOf(userId))
//        );

        cartItemRepository.deleteByUserId(Long.valueOf(userId));
    }

    // fallback methods should be same as the method it is protecting
    public boolean addToCartFallback(String userId, CartItemRequest cartItemRequest, Exception e)
    {
        System.out.println("Fallback called");
        return false;
    }
}
