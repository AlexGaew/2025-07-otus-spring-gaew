package ru.otus.hw.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthorController {

  private final AuthorService authorService;

  @GetMapping("/author")
  public List<AuthorDto> getAuthors() {
    return authorService.findAll();
  }
}
