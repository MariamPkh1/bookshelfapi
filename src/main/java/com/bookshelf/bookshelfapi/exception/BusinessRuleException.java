package com.bookshelf.bookshelfapi.exception;

// A request that is well-formed but breaks a library rule, e.g. lending a title with no copies left
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
