package com.example.Order_management.model.exception;

public class OrderRequestNotFoundException extends RuntimeException {
    public OrderRequestNotFoundException(String message) {
        super(message);
    }
}
