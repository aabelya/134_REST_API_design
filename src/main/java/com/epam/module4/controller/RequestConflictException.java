package com.epam.module4.controller;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(code = HttpStatus.CONFLICT)
public class RequestConflictException extends RuntimeException {

    private String remediationMessage;

    public RequestConflictException() {
    }

    public RequestConflictException(String message, String remediationMessage) {
        super(message);
        this.remediationMessage = remediationMessage;
    }

    public RequestConflictException(String message, String remediationMessage, Throwable cause) {
        super(message, cause);
        this.remediationMessage = remediationMessage;
    }

    public RequestConflictException(Throwable cause) {
        super(cause);
    }

    public RequestConflictException(String message, String remediationMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.remediationMessage = remediationMessage;
    }

}
