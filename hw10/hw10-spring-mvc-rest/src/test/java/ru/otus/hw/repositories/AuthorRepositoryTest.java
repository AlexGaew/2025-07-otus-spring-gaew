package ru.otus.hw.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Author;

@DisplayName("Репозиторий на основе Jpa для работы с авторами ")
@DataJpaTest
class AuthorRepositoryTest {

  @Autowired
  private AuthorRepository authorRepository;

  private List<Author> dbAuthors;

  @BeforeEach
  void setUp() {
    dbAuthors = getDbAuthors();
  }

  @DisplayName("должен загружать автора по id")
  @ParameterizedTest
  @MethodSource("getDbAuthors")
  void shouldReturnCorrectAuthorById(Author expectedAuthor) {
    var actualAuthor = authorRepository.findById(expectedAuthor.getId());

    assertThat(actualAuthor).isPresent()
        .get()
        .usingRecursiveComparison()
        .isEqualTo(expectedAuthor);
  }

  @DisplayName("должен загружать список всех авторов")
  @Test
  void shouldReturnCorrectAuthorsList() {
    var actualAuthors = authorRepository.findAll();
    var expectedAuthors = dbAuthors;

    assertThat(actualAuthors).usingRecursiveComparison()
        .isEqualTo(expectedAuthors);
  }

  private static List<Author> getDbAuthors() {
    return IntStream.range(1, 4).boxed()
        .map(id -> new Author(id, "Author_" + id))
        .toList();
  }
}