package com.epam.module4.controller;

import com.epam.module4.controller.validation.ValidationErrorResponse;
import com.epam.module4.service.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.Optional;

import static com.epam.module4.controller.validation.ValidationErrorResponse.from;

@Component
public class ExtendedErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        final Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        final Throwable error = super.getError(webRequest);
        if (error instanceof MethodArgumentNotValidException) {
            ValidationErrorResponse validationErrorResponse = from((MethodArgumentNotValidException) error);
            errorAttributes.put("validationFails", validationErrorResponse);
        } else if (error instanceof ConstraintViolationException) {
            ValidationErrorResponse validationErrorResponse = from((ConstraintViolationException) error);
            errorAttributes.put("validationFails", validationErrorResponse);
        } else if (error instanceof RequestConflictException) {
            Optional.ofNullable(error.getMessage()).ifPresent(msg -> errorAttributes.put("conflict", msg));
            Optional.ofNullable(((RequestConflictException) error).getRemediationMessage())
                    .ifPresent(msg -> errorAttributes.put("remediation", msg));
        } else if (error instanceof UserNotFoundException || error instanceof UserOperationNotAuthorizedException) {
            errorAttributes.put("details", error.getMessage());
        }
        return errorAttributes;
    }
}
