package com.ecommerce.user.exceptions;

public class UserNotFoundExceptions extends RuntimeException{
    public UserNotFoundExceptions(String message)
    {
        super(message);
    }
}
