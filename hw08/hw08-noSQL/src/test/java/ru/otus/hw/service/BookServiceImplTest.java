package ru.otus.hw.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;

@DataMongoTest
@Import(BookServiceImpl.class)
public class BookServiceImplTest {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private BookService bookService;

  @AfterEach
  void cleanUp() {
    bookRepository.deleteAll();
  }


  @Test
  void shouldInsertBook() {
    var title = "title";
    var author = new Author("author");
    var genre = new Genre("genre");
    Book aktualBook = new Book(title, author, genre);
    BookDto expectedBook = bookService.insert(title, author.getFullName(), List.of(genre));

    assertThat(aktualBook.getTitle()).isEqualTo(expectedBook.title());
    assertThat(aktualBook.getAuthor().getFullName()).isEqualTo(expectedBook.author().getFullName());
    assertThat(aktualBook.getGenres()).isEqualTo(expectedBook.genres());

  }
  @Test
  void shouldUpdateBook() {
    BookDto bookDto1 = bookService.insert("title_1", "Author1", List.of(new Genre("Genre1")));
    var title = "newTitle";
    var author = new Author("DDD");
    var genre = new Genre("GGG");
    BookDto actualBook = bookService.update(bookDto1.id(), title, author.getFullName(), List.of(genre));
    BookDto expectedBook = bookService.findById(bookDto1.id()).get();

    assertThat(actualBook).usingRecursiveComparison().isEqualTo(expectedBook);

  }

  @Test
  void shouldFindAllBooks() {
    BookDto bookDto1 = bookService.insert("title_1", "Author1", List.of(new Genre("Genre1")));
    BookDto bookDto2 = bookService.insert("title_2", "Author2", List.of(new Genre("Genre2")));
    BookDto bookDto3 = bookService.insert("title_3", "Author3", List.of(new Genre("Genre3")));
    List<BookDto> actual = List.of(bookDto1, bookDto2, bookDto3);
    List<BookDto> expectedBooks = bookService.findAll();

    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(expectedBooks);
  }

  @Test
  void shouldFindBookById() {
    BookDto actualBook = bookService.insert("title_1", "Author1", List.of(new Genre("Genre1")));
    Optional<BookDto> expectedBook = bookService.findById(actualBook.id());

    assertThat(expectedBook).isPresent()
        .get()
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(actualBook);
  }

  @Test
  void shouldDeleteById() {
    BookDto bookDto1 = bookService.insert("title_1", "Author1", List.of(new Genre("Genre1")));
    bookService.deleteById(bookDto1.id());

    assertThat(bookService.findById(bookDto1.id())).isEmpty();
  }

}
