package ru.otus.hw.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public String handleException(Exception ex) {
    log.error("Service error: {}", ex.getMessage());
    return "my-error";
  }

  @ExceptionHandler({EntityNotFoundException.class, NotFoundException.class})
  public String handleEntityNotFound(Exception ex, Model model) {
    log.warn("Entity not found: {}", ex.getMessage());
    model.addAttribute("errorMessage", "An unexpected error has occurred. Please try again later.");
    return "not-found";
  }
}
