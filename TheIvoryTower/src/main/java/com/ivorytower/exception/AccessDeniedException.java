package com.ivorytower.exception;

public class AccessDeniedException extends ServiceException {
    public AccessDeniedException(String message) {
        super(message);
    }
}