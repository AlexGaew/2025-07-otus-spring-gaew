package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookRequestDto {

  private Long id;

  @NotBlank(message = "Поле не должно быть пустым")
  @Size(min = 2, max = 10, message = "Размер текста от 2-х до 10 символов")
  private String title;

  private Long authorId;

  @NotEmpty(message = "У книги должен быть хотя бы 1 жанр")
  private List<Long> genreIds;

  public static BookRequestDto from(BookDto book) {
    BookRequestDto dto = new BookRequestDto();
    dto.id = book.getId();
    dto.title = book.getTitle();
    dto.authorId = book.getAuthor().id();
    dto.genreIds = book.getGenres().stream()
        .map(GenreDto::id)
        .toList();
    return dto;
  }

}
