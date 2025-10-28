package ru.otus.hw.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

@SpringBootTest
public class BookServiceImplTest {

  @Autowired
  private BookService bookService;

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenAccessingAuthor() {
    Optional<BookDto> bookDto = bookService.findById(1L);

    assertThat(bookDto).isPresent();
    assertThat(bookDto.get().author()).isNotNull();
  }

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenAccessingGenres() {
    Optional<BookDto> bookDto = bookService.findById(1L);

    assertThat(bookDto).isPresent();
    assertThat(bookDto.get().genres()).isNotEmpty();
  }
}
