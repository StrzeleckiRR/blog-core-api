package pl.mojastrona.common;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationEx(MethodArgumentNotValidException ex){
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> System.out.println(fieldError.getField() + " : " + fieldError.getDefaultMessage()));

        Map<String, String> fieldsErrorMap = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            if (fieldsErrorMap.put(fieldError.getField(), fieldError.getDefaultMessage()) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        return ResponseEntity.badRequest().body(fieldsErrorMap);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex){
        log.warn("handleHttpMessageNotReadableException", ex);
        return ResponseEntity.badRequest().body(new ErrorResponse("Invalid JSON"));
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex){
        log.warn("handleMethodArgumentTypeMismatchException", ex);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<Void> handleNotFoundException(RuntimeException ex){
        log.warn("handleNotFoundException", ex);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Void> handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex){
        log.warn("handleObjectOptimisticLockingFailureException", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}
