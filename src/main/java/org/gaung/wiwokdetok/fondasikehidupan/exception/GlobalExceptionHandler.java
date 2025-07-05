package org.gaung.wiwokdetok.fondasikehidupan.exception;

import org.gaung.wiwokdetok.fondasikehidupan.dto.WebResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<?>> handleGenericException(Exception ex) {
        WebResponse<?> response = WebResponse.builder()
                .errors("Internal Server Error")
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<WebResponse<?>> handleNotFound(NoHandlerFoundException ex) {
        WebResponse<?> response = WebResponse.builder()
                .errors("Not Found")
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<WebResponse<?>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        WebResponse<?> response = WebResponse.builder()
                .errors("Method Not Allowed")
                .build();

        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<?>> handleResponseStatusException(ResponseStatusException ex) {
        WebResponse<?> response = WebResponse.builder()
                .errors(ex.getReason())
                .build();

        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<WebResponse<?>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        WebResponse<?> response = WebResponse.builder()
                .errors("Missing or unsupported Content-Type")
                .build();

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<?>> handleMethodArgNotValid(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        WebResponse<?> response = WebResponse.builder()
                .errors(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
