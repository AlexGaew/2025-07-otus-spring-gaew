package ru.otus.hw.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Book;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

  private long id;

  private String title;

  private AuthorDto author;

  private List<GenreDto> genres;


  public static BookDto from(Book book) {
    return new BookDto(
        book.getId(),
        book.getTitle(),
        AuthorDto.from(book.getAuthor()),
        GenreDto.from(book.getGenres()));
  }
}
