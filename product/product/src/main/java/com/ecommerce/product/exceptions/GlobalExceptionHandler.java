package com.ecommerce.product.exceptions;



import com.ecommerce.product.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundExceptions.class)
    public ResponseEntity<?> handleCompanyNotFoundException(ProductNotFoundExceptions companyNotFoundExceptions)
    {
        ErrorResponse errorResponse=new ErrorResponse();

        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setDetails(companyNotFoundExceptions.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    //same as above, but we have to use @ControllerAdvice instead of @RestControllerAdvice

//    @ExceptionHandler(CompanyNotFoundExceptions.class)
//    @ResponseBody
//    public String handleCompanyNotFoundException(CompanyNotFoundExceptions companyNotFoundExceptions)
//    {
//        ErrorResponse errorResponse=new ErrorResponse();
//
//        errorResponse.setTime(LocalDateTime.now());
//        errorResponse.setMessage("Company with this id not found in database");
//        errorResponse.setDetails(companyNotFoundExceptions.getMessage());
//
//        return "No company found with this id";
//    }
}
