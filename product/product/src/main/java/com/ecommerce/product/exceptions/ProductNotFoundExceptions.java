package com.ecommerce.product.exceptions;

public class ProductNotFoundExceptions extends RuntimeException{
    public ProductNotFoundExceptions(String message)
    {
        super(message);
    }
}
