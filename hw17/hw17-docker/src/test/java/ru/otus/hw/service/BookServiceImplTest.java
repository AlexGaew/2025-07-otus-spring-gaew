package ru.otus.hw.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;

@DataJpaTest
@Import(BookServiceImpl.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class BookServiceImplTest {

  @Autowired
  private BookService bookService;

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenAccessingAuthorAndGenres() {
    assertDoesNotThrow(() -> bookService.findById(1L));
  }

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenFindAll() {
    assertDoesNotThrow(() -> bookService.findAll());
  }

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenInsert() {
    assertDoesNotThrow(() -> bookService.insert("New Book", 1L, List.of(1L, 2L)));
  }

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenUpdate() {
    assertDoesNotThrow(() -> bookService.update(1L, "Updated Book", 2L, List.of(2L)));
  }
}
