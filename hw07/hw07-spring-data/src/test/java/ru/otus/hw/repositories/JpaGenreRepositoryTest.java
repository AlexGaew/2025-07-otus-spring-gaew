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
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;

@DisplayName("Репозиторий на основе Jpa для работы с жанрами ")
@DataJpaTest
class JpaGenreRepositoryTest {

  @Autowired
  private GenreRepository genreRepository;

  private List<Genre> dbGenres;

  @BeforeEach
  void setUp() {
    dbGenres = getDbGenres();
  }

  @DisplayName("должен загружать жанры по ids")
  @Test
  void shouldReturnCorrectGenresByIds() {
    var expectedGenres = getDbGenres();
    List<Long> ids = expectedGenres.stream()
        .map(Genre::getId)
        .toList();
    var actualGenres = genreRepository.findAllById(ids);
    assertThat(actualGenres).usingRecursiveComparison()
        .isEqualTo(expectedGenres);
  }

  @DisplayName("должен загружать список всех жанров")
  @Test
  void shouldReturnCorrectGenresList() {
    var actualGenres = genreRepository.findAll();
    var expectedGenres = dbGenres;

    assertThat(actualGenres).usingRecursiveComparison()
        .isEqualTo(expectedGenres);
  }

  private static List<Genre> getDbGenres() {
    return IntStream.range(1, 7).boxed()
        .map(id -> new Genre(id, "Genre_" + id))
        .toList();
  }
}