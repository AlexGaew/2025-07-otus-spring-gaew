package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class, JpaGenreRepository.class, JpaAuthorRepository.class})
class JpaBookRepositoryTest {

  @Autowired
  private JpaBookRepository jpaBookRepository;

  @Autowired
  private EntityManager em;

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
    var actualBook = jpaBookRepository.findById(expectedBook.getId());
    assertThat(actualBook).isPresent()
        .get()
        .usingRecursiveComparison()
        .isEqualTo(expectedBook);
  }

  @DisplayName("должен загружать список всех книг")
  @Test
  void shouldReturnCorrectBooksList() {
    var actualBooks = jpaBookRepository.findAll();
    var expectedBooks = dbBooks;

    assertThat(actualBooks).usingRecursiveComparison()
        .ignoringFields("comments")
        .isEqualTo(expectedBooks);
    actualBooks.forEach(System.out::println);
  }

  @DisplayName("должен сохранять новую книгу")
  @Test
  void shouldSaveNewBook() {
    var expectedBook = new Book(0, "BookTitle_10500", dbAuthors.get(0),
        List.of(dbGenres.get(0), dbGenres.get(2)), new ArrayList<>());
    var returnedBook = jpaBookRepository.save(expectedBook);
    em.flush();
    em.clear();
    assertThat(returnedBook).isNotNull()
        .matches(book -> book.getId() > 0)
        .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

    assertThat(jpaBookRepository.findById(returnedBook.getId()))
        .isPresent()
        .get()
        .usingRecursiveComparison()
        .isEqualTo(returnedBook);
  }


  @DisplayName("должен сохранять измененную книгу")
  @Test
  void shouldSaveUpdatedBook() {
    var expectedBook = new Book(1L, "BookTitle_10500", dbAuthors.get(2),
        List.of(dbGenres.get(4), dbGenres.get(5)), new ArrayList<>());

    assertThat(jpaBookRepository.findById(expectedBook.getId()))
        .isPresent()
        .get()
        .isNotEqualTo(expectedBook);

    var returnedBook = jpaBookRepository.save(expectedBook);
    em.flush();
    em.clear();
    var actualBook = jpaBookRepository.findById(returnedBook.getId());

    assertThat(actualBook)
        .isPresent()
        .get()
        .usingRecursiveComparison()
        .ignoringFields("comments")
        .isEqualTo(expectedBook);
  }

  @DisplayName("должен удалять книгу по id ")
  @Test
  void shouldDeleteBook() {
    assertThat(jpaBookRepository.findById(2L)).isPresent();
    jpaBookRepository.deleteById(2L);
    em.flush();
    em.clear();
    assertThat(jpaBookRepository.findById(2L)).isEmpty();
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
        .map(id -> {
          Book book = new Book(
              id,
              "BookTitle_" + id,
              dbAuthors.get(id - 1),
              dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2),
              new ArrayList<>());

          List<Comment> comments = IntStream.range(0, 2)
              .mapToObj(comId -> new Comment(
                  id + comId * 3,
                  "Comment_" + (id + comId * 3),
                  book
              )).toList();
          book.getComments().addAll(comments);
          return book;
        }).toList();
  }

  private static List<Book> getDbBooks() {
    var dbAuthors = getDbAuthors();
    var dbGenres = getDbGenres();
    return getDbBooks(dbAuthors, dbGenres);
  }
}