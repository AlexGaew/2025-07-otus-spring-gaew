package ru.otus.hw.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

@DisplayName("Репозиторий на основе Jpa для работы с Жанрами ")
@DataJpaTest
@Import({JpaAuthorRepository.class})
class JpaAuthorRepositoryTest {

  @Autowired
  private JpaAuthorRepository jpaAuthorRepository;

  private List<Author> dbAuthor;

  @BeforeEach
  void setUp() {
    dbAuthor = getDbAuthor();
  }

  @DisplayName("должен загружать autor по id")
  @Test
  void shouldReturnCorrectAuthorById() {
    var expectedAuthor = getDbAuthor().get(0);

    var actualAuthor = jpaAuthorRepository.findById(1).get();

    assertThat(actualAuthor).usingRecursiveComparison()
        .isEqualTo(expectedAuthor);
    System.out.println(actualAuthor);
  }

  @DisplayName("должен загружать список всех authors")
  @Test
  void shouldReturnCorrectAuthorsList() {
    var actualAuthors = jpaAuthorRepository.findAll();
    var expectedAuthors = dbAuthor;

    assertThat(actualAuthors).usingRecursiveComparison()
        .isEqualTo(expectedAuthors);
    actualAuthors.forEach(System.out::println);
  }

  private static List<Author> getDbAuthor() {
    return IntStream.range(1, 4).boxed()
        .map(id -> new Author(id, "Author_" + id))
        .toList();
  }
}