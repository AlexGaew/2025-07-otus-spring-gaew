package ru.otus.hw.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaGenreRepository;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;

@DataJpaTest
@Import({
    BookServiceImpl.class,
    JpaBookRepository.class,
    JpaAuthorRepository.class,
    JpaGenreRepository.class
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class BookServiceImplTest {

  @Autowired
  private BookService bookService;

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenAccessingAuthorAndGenres() {
    Optional<BookDto> bookDto = bookService.findById(1L);

    assertDoesNotThrow(() -> {
      bookDto.get().author();
      bookDto.get().author().fullName();
      bookDto.get().genres();
      bookDto.get().genres().get(0).name();
    });
  }

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenFindAll() {
    List<BookDto> bookDto = bookService.findAll();

    assertDoesNotThrow(() -> {
      bookDto.get(0).author();
      bookDto.get(0).author().fullName();
      bookDto.get(0).genres();
      bookDto.get(0).genres().get(0).name();
    });
  }

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenInsert() {
    BookDto bookDto = bookService.insert("New Book", 1L, List.of(1L, 2L));

    assertDoesNotThrow(() -> {
      bookDto.author();
      bookDto.author().fullName();
      bookDto.genres();
      bookDto.genres().get(0).name();
    });
  }

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenUpdate() {
    BookDto bookDto = bookService.update(1L, "Updated Book", 2L, List.of(2L));

    assertDoesNotThrow(() -> {
      bookDto.author();
      bookDto.author().fullName();
      bookDto.genres();
      bookDto.genres().get(0).name();
    });
  }
}
