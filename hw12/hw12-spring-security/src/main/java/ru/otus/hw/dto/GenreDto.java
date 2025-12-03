package ru.otus.hw.dto;

import java.util.List;
import ru.otus.hw.models.Genre;

public record GenreDto(long id, String name) {

  public static List<GenreDto> from(List<Genre> genres) {
    return genres.stream()
        .map(g -> new GenreDto(g.getId(), g.getName()))
        .toList();
  }
}
