package ru.otus.hw.dto;

import java.util.List;
import ru.otus.hw.models.Genre;

public record GenreDto(String id, String name) {

  public static GenreDto from(Genre genre) {
    return new GenreDto(genre.getId(), genre.getName());
  }

  public static List<GenreDto> from(List<Genre> genres) {
    return genres.stream()
        .map(GenreDto::from)
        .toList();
  }
}