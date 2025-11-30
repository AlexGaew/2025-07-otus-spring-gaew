package ru.otus.hw.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorServiceImpl;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;
import ru.otus.hw.services.GenreServiceImpl;

@DataMongoTest
@Import({BookServiceImpl.class, AuthorServiceImpl.class, GenreServiceImpl.class})
public class BookServiceImplTest {

  @Autowired
  private ReactiveMongoTemplate mongoTemplate;

  @Autowired
  private BookService bookService;

  private List<Author> dbAuthors;

  private List<Genre> dbGenres;

  private List<Book> dbBooks;

  @BeforeEach
  void setUp() {
    dbAuthors = getDbAuthors();
    dbGenres = getDbGenres();
    dbBooks = getDbBooks();
    mongoTemplate.insertAll(dbAuthors).blockLast();
    mongoTemplate.insertAll(dbGenres).blockLast();
    mongoTemplate.insertAll(dbBooks).blockLast();
  }

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection(Author.class).block();
    mongoTemplate.dropCollection(Genre.class).block();
    mongoTemplate.dropCollection(Book.class).block();
  }


  @DisplayName("должен создавать новую книгу")
  @Test
  void shouldInsertBook() {
    var expectedBook = new Book("BookTitle_10500", dbAuthors.get(0), List.of(dbGenres.get(0), dbGenres.get(1)));
    var returnedBook = bookService.insert(expectedBook.getTitle(), expectedBook.getAuthor().getId(),
        expectedBook.getGenres().stream().map(Genre::getId).toList()).block();

    assertThat(returnedBook).isNotNull()
        .matches(book -> !book.getId().isEmpty())
        .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

    Book actualBook = mongoTemplate.findById(returnedBook.getId(), Book.class).block();
    assertThat(actualBook).isNotNull();
    assertThat(actualBook.getId()).isEqualTo(returnedBook.getId());
    assertThat(actualBook.getTitle()).isEqualTo(returnedBook.getTitle());
    assertThat(actualBook.getAuthor().getId()).isEqualTo(returnedBook.getAuthor().id());
    assertThat(actualBook.getGenres()).hasSize(returnedBook.getGenres().size());

  }

  @DisplayName("должен загружать книгу по id")
  @ParameterizedTest
  @MethodSource("getDbBooks")
  void shouldReturnCorrectBookById(Book expectedBook) {
    var actualBook = bookService.findById(expectedBook.getId()).block();
    var expectedDto = BookDto.from(expectedBook);

    assertThat(actualBook)
        .usingRecursiveComparison()
        .isEqualTo(expectedDto);
  }

  @DisplayName("должен загружать список всех книг")
  @Test
  void shouldReturnCorrectBooksList() {
    var expectedBooks = dbBooks.stream()
        .map(BookDto::from)
        .toList();

    StepVerifier.create(bookService.findAll())
        .expectNextSequence(expectedBooks)
        .verifyComplete();
  }

  @DisplayName("должен сохранять измененную книгу")
  @Test
  void shouldSaveUpdatedBook() {
    var expectedBook = new Book("1", "BookTitle_105000", dbAuthors.get(0), List.of(dbGenres.get(0), dbGenres.get(1)));

    BookDto saveBook = bookService.update("1", "BookTitle_105000", "1", List.of("1", "2")).block();

    Book actualBook = mongoTemplate.findById(saveBook.getId(), Book.class).block();
    assertThat(actualBook).isNotNull();
    assertThat(actualBook.getId()).isEqualTo(expectedBook.getId());
    assertThat(actualBook.getTitle()).isEqualTo(expectedBook.getTitle());
    assertThat(actualBook.getAuthor().getId()).isEqualTo(expectedBook.getAuthor().getId());
    assertThat(actualBook.getGenres()).hasSize(expectedBook.getGenres().size());

  }

  @DisplayName("должен удалять книгу по id ")
  @Test
  void shouldDeleteBook() {
    assertThat(bookService.findById("1").block()).isNotNull();

    bookService.deleteById("1").block();

    assertThat(bookService.findById("1").block()).isNull();
  }

  private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
    return IntStream.range(1, 4).boxed()
        .map(id -> new Book(
            String.valueOf(id),
            "BookTitle_" + id,
            dbAuthors.get(id - 1),
            dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
        )).toList();
  }

  private static List<Book> getDbBooks() {
    var dbAuthors = getDbAuthors();
    var dbGenres = getDbGenres();
    return getDbBooks(dbAuthors, dbGenres);
  }

  private static List<Author> getDbAuthors() {
    return IntStream.range(1, 4).boxed()
        .map(id -> new Author(String.valueOf(id), "Author_" + id))
        .toList();
  }

  private static List<Genre> getDbGenres() {
    return IntStream.range(1, 7).boxed()
        .map(id -> new Genre(String.valueOf(id), "Genre_" + id))
        .toList();
  }

}
