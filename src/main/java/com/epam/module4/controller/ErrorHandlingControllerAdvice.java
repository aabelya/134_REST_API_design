package com.epam.module4.controller;

import com.epam.module4.service.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @Autowired
    BasicErrorController basicErrorController;

    Pattern uniquenessViolationPattern = Pattern.compile("Unique index or primary key violation.*?VALUES\\s+\\((.*?)\\)");

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Map<String, Object> handleSQLConstraintValidationException(HttpServletRequest httpServletRequest, SQLIntegrityConstraintViolationException e) {
        Map<String, Object> details = extendsAttributes(httpServletRequest, "details", extractDetails(e));
        details.put("status", HttpStatus.BAD_REQUEST.value());
        return details;
    }

    private String extractDetails(SQLIntegrityConstraintViolationException e) {
        String message = e.getMessage();
        if (message == null) {
            return null;
        }
        Matcher matcher = uniquenessViolationPattern.matcher(message);
        if (matcher.find()) {
            return "values already exists" + Arrays.stream(matcher.group(1).split("\\s+,\\s+"))
                    .map(String::trim).map(s -> s.substring(s.indexOf("'"), s.lastIndexOf("'") + 1))
                    .collect(Collectors.joining(", ", "[ ", " ]"));
        }
        return null;
    }

    private Map<String, Object> extendsAttributes(HttpServletRequest httpServletRequest, String key, Object value) {
        return Optional.of(basicErrorController.error(httpServletRequest))
                .map(HttpEntity::getBody)
                .map(attr -> ofNullable(value)
                        .map(v -> extendsAttributes(attr, key, v))
                        .orElse(attr))
                .orElse(null);
    }

    private Map<String, Object> extendsAttributes(Map<String, Object> attributes, String key, Object value) {
        attributes.put(key, value);
        return attributes;
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    Map<String, Object> handleSQLException(HttpServletRequest httpServletRequest, SQLException e) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("sqlError", e.getErrorCode());
        return details;
    }


}
