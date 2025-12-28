package com.example.demo.exceptions;

import com.example.demo.exceptions.customHandlers.EmailAlreadyExists;
import com.example.demo.exceptions.customHandlers.RefreshTokenExpired;
import com.example.demo.exceptions.customHandlers.RefreshTokenReuseDetected;
import com.example.demo.exceptions.customHandlers.ResourseNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExists.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExists(EmailAlreadyExists ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(ResourseNotFound.class)
    public ResponseEntity<ApiErrorResponse> handleResourseNotFound(ResourseNotFound ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(RefreshTokenExpired.class)
    public ResponseEntity<ApiErrorResponse> handleRefreshTokenExpired(BussinessException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(RefreshTokenReuseDetected.class)
    public ResponseEntity<ApiErrorResponse> handleRefreshTokenRevoked(BussinessException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBussinessException(BussinessException ex){
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                ));
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex){
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        errors
                ));
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }



}
