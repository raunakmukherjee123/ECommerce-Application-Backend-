package com.ecommerce.product.controller;


import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.exceptions.ProductNotFoundExceptions;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;


    @PostMapping("/create")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest)
    {
        return new ResponseEntity(productService.createProduct(productRequest), HttpStatus.CREATED);
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<ProductResponse>> fetchAllProducts()
    {
        return ResponseEntity.ok(productService.getProducts());
    }

    @GetMapping("/fetch/{id}")
    public ResponseEntity<ProductResponse> fetchProductById(@PathVariable Long id)
    {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(()->new ProductNotFoundExceptions("No product found"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,@RequestBody ProductRequest productRequest)
    {
        return productService.updateProduct(id,productRequest)
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id)
    {
         productService.deleteProduct(id);

         return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword)
    {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }
}
