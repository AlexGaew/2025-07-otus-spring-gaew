package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с книгами ")
@DataJpaTest
class JpaBookRepositoryTest {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  private List<Author> dbAuthors;

  private List<Genre> dbGenres;

  private List<Book> dbBooks;

  @BeforeEach
  void setUp() {
    dbAuthors = getDbAuthors();
    dbGenres = getDbGenres();
    dbBooks = getDbBooks(dbAuthors, dbGenres);
  }

  @DisplayName("должен загружать книгу по id")
  @ParameterizedTest
  @MethodSource("getDbBooks")
  void shouldReturnCorrectBookById(Book expectedBook) {

    var actualBook = bookRepository.findById(expectedBook.getId());

    assertThat(actualBook).isPresent()
        .get()
        .usingRecursiveComparison()
        .isEqualTo(expectedBook);
  }

  @DisplayName("должен загружать список всех книг")
  @Test
  void shouldReturnCorrectBooksList() {
    var actualBooks = bookRepository.findAll();
    var expectedBooks = dbBooks;

    assertThat(actualBooks).usingRecursiveComparison()
        .isEqualTo(expectedBooks);
    actualBooks.forEach(System.out::println);
  }

  @DisplayName("должен сохранять новую книгу")
  @Test
  void shouldSaveNewBook() {
    var expectedBook = new Book(0, "BookTitle_10500", dbAuthors.get(0),
        List.of(dbGenres.get(0), dbGenres.get(2)));
    var returnedBook = bookRepository.save(expectedBook);

    assertThat(returnedBook).isNotNull()
        .matches(book -> book.getId() > 0)
        .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

    Book actualBook = testEntityManager.find(Book.class, returnedBook.getId());
    assertThat(actualBook).isNotNull();
    assertThat(actualBook.getId()).isEqualTo(returnedBook.getId());
    assertThat(actualBook.getTitle()).isEqualTo(returnedBook.getTitle());
    assertThat(actualBook.getAuthor().getId()).isEqualTo(returnedBook.getAuthor().getId());
    assertThat(actualBook.getGenres()).hasSize(returnedBook.getGenres().size());

  }


  @DisplayName("должен сохранять измененную книгу")
  @Test
  void shouldSaveUpdatedBook() {
    var expectedBook = new Book(1L, "BookTitle_10500", dbAuthors.get(2),
        List.of(dbGenres.get(4), dbGenres.get(5)));

    Book bookBeforeUpdate = testEntityManager.find(Book.class, expectedBook.getId());
    assertThat(bookBeforeUpdate).isNotNull();
    assertThat(bookBeforeUpdate.getTitle()).isNotEqualTo(expectedBook.getTitle());


    var returnedBook = bookRepository.save(expectedBook);

    Book actualBook = testEntityManager.find(Book.class, returnedBook.getId());
    assertThat(actualBook).isNotNull();
    assertThat(actualBook.getId()).isEqualTo(expectedBook.getId());
    assertThat(actualBook.getTitle()).isEqualTo(expectedBook.getTitle());
    assertThat(actualBook.getAuthor().getId()).isEqualTo(expectedBook.getAuthor().getId());
    assertThat(actualBook.getGenres()).hasSize(expectedBook.getGenres().size());

  }

  @DisplayName("должен удалять книгу по id ")
  @Test
  void shouldDeleteBook() {
    assertThat(testEntityManager.find(Book.class, 2L)).isNotNull();

    bookRepository.deleteById(2L);
    assertThat(testEntityManager.find(Book.class, 2L)).isNull();

  }

  private static List<Author> getDbAuthors() {
    return IntStream.range(1, 4).boxed()
        .map(id -> new Author(id, "Author_" + id))
        .toList();
  }

  private static List<Genre> getDbGenres() {
    return IntStream.range(1, 7).boxed()
        .map(id -> new Genre(id, "Genre_" + id))
        .toList();
  }

  private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
    return IntStream.range(1, 4).boxed()
        .map(id -> new Book(
            id,
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
}