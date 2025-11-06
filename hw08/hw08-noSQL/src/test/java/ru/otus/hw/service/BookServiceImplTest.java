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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.AuthorServiceImpl;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;
import ru.otus.hw.services.GenreServiceImpl;

@DataMongoTest
@Import({BookServiceImpl.class, AuthorServiceImpl.class, GenreServiceImpl.class})
public class BookServiceImplTest {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private AuthorRepository authorRepository;

  @Autowired
  private GenreRepository genreRepository;

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
    authorRepository.saveAll(dbAuthors);
    genreRepository.saveAll(dbGenres);
    bookRepository.saveAll(dbBooks);
  }

  @AfterEach
  void cleanUp() {
    bookRepository.deleteAll();
  }


  @DisplayName("должен создавать новую книгу")
  @Test
  void shouldInsertBook() {
    var expectedBook = new Book("BookTitle_10500", dbAuthors.get(0), List.of(dbGenres.get(0), dbGenres.get(1)));
    var returnedBook = bookService.insert("BookTitle_10500", "1", List.of("1", "2"));

    assertThat(returnedBook).isNotNull()
        .matches(book -> !book.id().isEmpty())
        .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

    Book actualBook = bookRepository.findById(returnedBook.id()).get();
    assertThat(actualBook).isNotNull();
    assertThat(actualBook.getId()).isEqualTo(returnedBook.id());
    assertThat(actualBook.getTitle()).isEqualTo(returnedBook.title());
    assertThat(actualBook.getAuthor().getId()).isEqualTo(returnedBook.author().getId());
    assertThat(actualBook.getGenres()).hasSize(returnedBook.genres().size());

  }

  @DisplayName("должен загружать книгу по id")
  @ParameterizedTest
  @MethodSource("getDbBooks")
  void shouldReturnCorrectBookById(Book expectedBook) {

    var actualBook = bookService.findById(expectedBook.getId());

    assertThat(actualBook).isPresent()
        .get()
        .usingRecursiveComparison()
        .isEqualTo(expectedBook);
  }

  @DisplayName("должен загружать список всех книг")
  @Test
  void shouldReturnCorrectBooksList() {
    var actualBooks = bookService.findAll();
    var expectedBooks = dbBooks;

    assertThat(actualBooks).usingRecursiveComparison()
        .isEqualTo(expectedBooks);
    actualBooks.forEach(System.out::println);
  }

  @DisplayName("должен сохранять измененную книгу")
  @Test
  void shouldSaveUpdatedBook() {
    var expectedBook = new Book("1", "BookTitle_105000", dbAuthors.get(0), List.of(dbGenres.get(0), dbGenres.get(1)));

    BookDto saveBook = bookService.update("1", "BookTitle_105000", "1", List.of("1", "2"));

    Book actualBook = bookRepository.findById(saveBook.id()).get();
    assertThat(actualBook).isNotNull();
    assertThat(actualBook.getId()).isEqualTo(expectedBook.getId());
    assertThat(actualBook.getTitle()).isEqualTo(expectedBook.getTitle());
    assertThat(actualBook.getAuthor().getId()).isEqualTo(expectedBook.getAuthor().getId());
    assertThat(actualBook.getGenres()).hasSize(expectedBook.getGenres().size());

  }

  @DisplayName("должен удалять книгу по id ")
  @Test
  void shouldDeleteBook() {

    assertThat(bookService.findById("1")).isNotNull();

    bookService.deleteById("1");
    assertThat(bookService.findById("1")).isEmpty();

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
