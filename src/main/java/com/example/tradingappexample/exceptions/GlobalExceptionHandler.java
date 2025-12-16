package com.example.tradingappexample.exceptions;


import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({TradeNotFoundException.class,
            OrderNotFoundException.class})
    public ProblemDetail handleNotFound(TradeNotFoundException ex){
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Resource Not Found");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "NOT_FOUND");
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationError(MethodArgumentNotValidException ex) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation failed");
        problem.setDetail("One or more fields are invalid");

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        problem.setProperty("errors", errors);
        problem.setProperty("code", "VALIDATION_ERROR");

        return problem;
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, OptimisticLockException.class})
    public ProblemDetail handleOptimisticLock(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail("The trade was modified by another request. Reload and retry.");
        pd.setProperty("code", "OPTIMISTIC_LOCK_CONFLICT");
        return pd;
    }



    @ExceptionHandler(InvalidFillQuantityException.class)
    public ProblemDetail handleInvalidFill(InvalidFillQuantityException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid fill quantity");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "INVALID_FILL_QTY");
        return pd;
    }

    @ExceptionHandler({
            OrderAlreadyClosedException.class,
            OrderAlreadyFilledException.class,
            OrderAlreadyCancelledException.class,
            OrderOverfillException.class
    })
    public ProblemDetail handleOrderConflict(RuntimeException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Order state conflict");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "ORDER_CONFLICT");
        return pd;
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ProblemDetail handleMissingHeader(MissingRequestHeaderException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Missing request header");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "MISSING_HEADER");
        return pd;
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail(ex.getMessage());
        pd.setProperty("code", "VALIDATION_ERROR");
        return pd;
    }
}
