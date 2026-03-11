package com.ecommerce.order.controller;



import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestHeader("X-User-ID") String userId,
                                            @RequestBody CartItemRequest cartItemRequest) {
        if (cartService.addToCart(userId, cartItemRequest)) {
            return new ResponseEntity<>("Cart added", HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().body("Product out of stock or User not found");
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeCart(@RequestHeader("X-User-ID") String userId,
                                             @PathVariable Long productId)
    {
        if(cartService.deleteItemFromCart(userId,productId))
        {
            return ResponseEntity.ok("Cart item removed");
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCarts(@RequestHeader("X-User-ID") String userId)
    {
        return ResponseEntity.ok(cartService.getCarts(userId));
    }
}
