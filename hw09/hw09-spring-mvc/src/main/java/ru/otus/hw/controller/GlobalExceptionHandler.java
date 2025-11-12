package ru.otus.hw.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public String handleException(Exception ex) {
    System.out.println(ex.getMessage());
    return "my-error";
  }

  @ExceptionHandler({EntityNotFoundException.class, NotFoundException.class})
  public String handleEntityNotFound(Exception ex, Model model) {
    model.addAttribute("errorMessage", ex.getMessage());
    return "not-found";
  }
}
