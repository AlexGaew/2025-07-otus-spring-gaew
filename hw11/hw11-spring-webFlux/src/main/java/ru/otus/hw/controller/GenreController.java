package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GenreController {

  private final GenreService genreService;


  @GetMapping("/genre")
  public Flux<GenreDto> getGenres() {
    return genreService.findAll();
  }
}
