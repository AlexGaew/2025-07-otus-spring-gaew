package ru.otus.hw.dto;

import java.util.List;
import ru.otus.hw.models.Book;

public record BookDto(long id, String title, AuthorDto author, List<GenreDto> genres, List<CommentDto> comments) {

  public static BookDto from(Book book) {
    return new BookDto(
        book.getId(),
        book.getTitle(),
        AuthorDto.from(book.getAuthor()),
        GenreDto.from(book.getGenres()),
        CommentDto.fromList(book.getComments()));
  }
}
