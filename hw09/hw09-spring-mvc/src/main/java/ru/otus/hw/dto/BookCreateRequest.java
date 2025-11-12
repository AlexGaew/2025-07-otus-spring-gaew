package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookCreateRequest {

  @NotBlank(message = "Поле не должно быть пустым")
  @Size(min = 2, max = 10, message = "Размер теста от 2-х до 10 символов")
  private String title;

  private Long authorId;

  @NotEmpty(message = "У книги должен быть хотя бы 1 жанр")
  private List<Long> genreIds;

}
