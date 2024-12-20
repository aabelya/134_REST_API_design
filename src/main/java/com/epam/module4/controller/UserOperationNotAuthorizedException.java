package com.epam.module4.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class UserOperationNotAuthorizedException extends RuntimeException {

    public UserOperationNotAuthorizedException() {
    }

    public UserOperationNotAuthorizedException(String message) {
        super(message);
    }

    public UserOperationNotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserOperationNotAuthorizedException(Throwable cause) {
        super(cause);
    }

    public UserOperationNotAuthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
