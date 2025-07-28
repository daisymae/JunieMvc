package com.cherylorcutt.juniemvc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors
     * 
     * @param ex the MethodArgumentNotValidException
     * @return a map of field errors with HTTP status 400 (BAD_REQUEST)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle CustomerNotFoundException
     * 
     * @param ex the CustomerNotFoundException
     * @return a ProblemDetail with HTTP status 404 (NOT_FOUND)
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Customer Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }
    
    /**
     * Handle BeerNotFoundException
     * 
     * @param ex the BeerNotFoundException
     * @return a ProblemDetail with HTTP status 404 (NOT_FOUND)
     */
    @ExceptionHandler(BeerNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleBeerNotFoundException(BeerNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Beer Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }
    
    /**
     * Handle OrderNotFoundException
     * 
     * @param ex the OrderNotFoundException
     * @return a ProblemDetail with HTTP status 404 (NOT_FOUND)
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleOrderNotFoundException(OrderNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Order Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }
    
    /**
     * Handle InvalidOrderStateException
     * 
     * @param ex the InvalidOrderStateException
     * @return a ProblemDetail with HTTP status 422 (UNPROCESSABLE_ENTITY)
     */
    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<ProblemDetail> handleInvalidOrderStateException(InvalidOrderStateException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setTitle("Invalid Order State");
        if (ex.getCurrentStatus() != null) {
            problemDetail.setProperty("currentStatus", ex.getCurrentStatus());
        }
        if (ex.getTargetStatus() != null) {
            problemDetail.setProperty("targetStatus", ex.getTargetStatus());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }
}