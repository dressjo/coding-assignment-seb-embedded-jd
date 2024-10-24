package se.seb.embedded.coding_assignment;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> invalidMessage(HttpMessageNotReadableException ex) {
        String message = switch(ex.getCause()) {
            case InvalidFormatException e -> "Invalid json for field with value [%s]".formatted(e.getValue());
            default -> "Invalid json [%s]".formatted(ex.getMessage());
        };
        return ResponseEntity
            .of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message))
            .build();
    }
}