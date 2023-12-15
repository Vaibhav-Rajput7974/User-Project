package com.example.GraplerEnhancemet.custom_exception;
public class DuplicateCompanyException extends RuntimeException {
    public DuplicateCompanyException(String message) {
        super(message);
    }
}