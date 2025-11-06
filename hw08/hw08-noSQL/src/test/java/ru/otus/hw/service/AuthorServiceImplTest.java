package ru.otus.hw.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.AuthorServiceImpl;

@DataMongoTest
@Import(AuthorServiceImpl.class)
class AuthorRepositoryTest {

  @Autowired
  private AuthorService authorService;

  @Autowired
  private AuthorRepository authorRepository;

  private List<Author> dbAuthors;

  @BeforeEach
  void setUp() {
    dbAuthors = getDbAuthors();
    authorRepository.saveAll(dbAuthors);
  }

  @DisplayName("должен загружать автора по id")
  @ParameterizedTest
  @MethodSource("getDbAuthors")
  void shouldReturnCorrectAuthorById(Author expectedAuthor) {
    var actualAuthor = authorService.findById(expectedAuthor.getId());

    assertThat(actualAuthor).isPresent()
        .get()
        .usingRecursiveComparison()
        .isEqualTo(expectedAuthor);
  }

  @DisplayName("должен загружать список всех авторов")
  @Test
  void shouldReturnCorrectAuthorsList() {
    var actualAuthors = authorService.findAll();
    var expectedAuthors = dbAuthors;

    assertThat(actualAuthors).usingRecursiveComparison()
        .isEqualTo(expectedAuthors);
  }

  private static List<Author> getDbAuthors() {
    return IntStream.range(1, 4).boxed()
        .map(id -> new Author(String.valueOf(id), "Author_" + id))
        .toList();
  }
}