package com.ecommerce.product.service;


import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.exceptions.ProductNotFoundExceptions;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product=new Product();

        updateProductFromRequestDto(product,productRequest);

        Product savedProduct = productRepository.save(product);

        return mapToProductResponse(savedProduct);
    }


    public Optional<ProductResponse> updateProduct(Long id, ProductRequest productRequest) {
       return productRepository.findById(id)
                .map(product -> {
                    updateProductFromRequestDto(product, productRequest);
                    Product savedProduct=productRepository.save(product);
                    return mapToProductResponse(savedProduct);
                });
    }

    public List<ProductResponse> getProducts() {
        List<Product> products= productRepository.findByIsActiveTrue();

        if(products==null)
        {
            throw new ProductNotFoundExceptions("No product found in the database");
        }

       return products.stream()
                .map(product ->mapToProductResponse(product))
               .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product savedProduct) {
        ProductResponse productResponse=new ProductResponse();

        productResponse.setDescription(savedProduct.getDescription());
        productResponse.setName(savedProduct.getName());
        productResponse.setPrice(savedProduct.getPrice());
        productResponse.setCategory(savedProduct.getCategory());
        productResponse.setImageUrl(savedProduct.getImageUrl());
        productResponse.setStockQuantity(savedProduct.getStockQuantity());
        productResponse.setIsActive(savedProduct.getIsActive());

        return productResponse;
    }

    private void updateProductFromRequestDto(Product product, ProductRequest productRequest) {
        product.setDescription(productRequest.getDescription());
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setStockQuantity(productRequest.getStockQuantity());
    }

    public void deleteProduct(Long id) {
       // productRepository.deleteById(id);

        Product product=productRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Product not found"));

        product.setIsActive(false);

        productRepository.save(product);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    public Optional<ProductResponse> getProductById(Long id) {
        return productRepository.findByIdAndIsActiveTrue(id)
                .map(product -> mapToProductResponse(product));
    }
}
