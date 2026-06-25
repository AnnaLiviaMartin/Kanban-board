package de.hsrm.master.concurrency.kanbanboard.exception;

import jakarta.persistence.OptimisticLockException;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestController
@RestControllerAdvice
public class GlobalExceptionHandler implements ErrorController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorType.VALIDATION_ERROR, "Eingabefehler", fieldErrors));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ErrorType.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(WipLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleWipLimit(WipLimitExceededException ex) {
        Map<String, Object> details = Map.of("wipLimit", ex.getWipLimit(), "currentCount", ex.getCurrentCount());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ErrorType.WIP_LIMIT_EXCEEDED, ex.getMessage(), details));
    }

    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class, OptimisticLockConflictException.class})
    public ResponseEntity<ErrorResponse> handleOptimisticLock(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ErrorType.OPTIMISTIC_LOCK_CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(TaskLockedException.class)
    public ResponseEntity<ErrorResponse> handleTaskLocked(TaskLockedException ex) {
        Map<String, Object> details = Map.of("taskId", ex.getTaskId(), "lockedBySession", ex.getLockedBySession());
        return ResponseEntity.status(HttpStatus.LOCKED).body(new ErrorResponse(ErrorType.TASK_LOCKED, ex.getMessage(), details));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorType.VALIDATION_ERROR, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ErrorType.INTERNAL_ERROR, ex.getMessage()));
    }
}
