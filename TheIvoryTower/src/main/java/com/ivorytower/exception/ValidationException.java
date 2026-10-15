package com.ivorytower.exception;

import java.util.LinkedHashMap;
import java.util.Map;

public class ValidationException extends ServiceException {
    private final Map<String, String> errors;

    public ValidationException(Map<String, String> errors) {
        super("Форма заполнена неверно");
        this.errors = new LinkedHashMap<>(errors);
    }
    public Map<String, String> getErrors() {
        return errors;
    }
}