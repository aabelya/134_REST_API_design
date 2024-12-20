package com.epam.module4.controller.validation;

import jakarta.validation.ConstraintViolationException;
import lombok.Data;
import lombok.Singular;
import lombok.Value;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationErrorResponse {

    @Singular
    private List<Violation> violations = new ArrayList<>();

    public void addViolation(String fieldName, String message) {
        violations.add(Violation.of(fieldName, message));
    }

    public static ValidationErrorResponse from(MethodArgumentNotValidException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        e.getBindingResult().getFieldErrors().forEach(fe ->
                error.addViolation(fe.getField(), fe.getDefaultMessage()));
        return error;
    }

    public static ValidationErrorResponse from(ConstraintViolationException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        e.getConstraintViolations().forEach(v ->
                error.addViolation(v.getPropertyPath().toString(), v.getMessage()));
        return error;
    }

    @Value(staticConstructor = "of")
    public static class Violation {

        String fieldName;
        String message;

    }


}
