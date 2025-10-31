package ru.otus.hw.dto;

import java.util.List;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

public record BookDto(String id, String title, Author author, List<Genre> genres) {

  public static BookDto from(Book book) {
    return new BookDto(
        book.getId(),
        book.getTitle(),
        book.getAuthor(),
        book.getGenres());
  }
}
