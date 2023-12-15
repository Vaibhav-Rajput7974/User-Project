package com.example.GraplerEnhancemet.custom_exception;
import com.example.GraplerEnhancemet.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid input Please provide a valid integer value in the URL";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<String>(false, null, message));
    }

    @ExceptionHandler(DuplicateCompanyException.class)
    public ResponseEntity<ApiResponse<String>> handleDuplicateCompanyException(DuplicateCompanyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<String>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
/*
BindingResult contains detailed information about the validation errors, including the error messages.
getFieldErrors() -> returns the first field-specific error in the BindingResult
 */
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, errors, "Invalid Data"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // Customize the error response with an error message.
        String errorMessage = "Invalid Structure type or request body: " + ex.getRootCause().getMessage(); // Get the root cause message
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, errorMessage));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Internal Server Error"));
    }
//
//    @ExceptionHandler(NoSuchElementException.class)
//    public ResponseEntity<String> handleResourceNotFoundException(NoSuchElementException ex) {
//        return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(DataAccessException.class)
//    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
//        return new ResponseEntity<>("An error occurred while accessing data.", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @ExceptionHandler(NumberFormatException.class)
//    public ResponseEntity<String> handleNumberFormatException(NumberFormatException ex) {
//        return new ResponseEntity<>("Invalid input provided.", HttpStatus.BAD_REQUEST);
//    }
}
