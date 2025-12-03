package ru.otus.hw.dto;

import ru.otus.hw.models.Author;

public record AuthorDto(long id, String fullName) {

  public static AuthorDto from(Author author) {
    return new AuthorDto(
        author.getId(),
        author.getFullName()
    );
  }
}
