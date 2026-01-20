package ru.otus.hw.controller;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotFoundException;

@RestControllerAdvice(assignableTypes = {BookController.class, AuthorController.class,
    GenreController.class, CommentController.class})
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler({EntityNotFoundException.class, NotFoundException.class})
  public ResponseEntity<Map<String, String>> handleEntityNotFound(Exception ex) {
    log.warn("Entity not found: {}", ex.getMessage());
    return ResponseEntity.status(404).body(Map.of("error", "An unexpected error has occurred. Please try again later."));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleException(Exception ex) {
    log.error("Service error: {}", ex.getMessage());
    return ResponseEntity.status(500).body(Map.of("error", "Server error"));
  }
}
