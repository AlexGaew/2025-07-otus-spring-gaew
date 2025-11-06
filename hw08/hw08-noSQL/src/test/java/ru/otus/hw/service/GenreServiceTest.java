package ru.otus.hw.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.GenreServiceImpl;

@DataMongoTest
@Import(GenreServiceImpl.class)
class GenreServiceImplTest {

  @Autowired
  private GenreRepository genreRepository;

  @Autowired
  private GenreService genreService;

  private List<Genre> dbGenres;

  @BeforeEach
  void setUp() {
    dbGenres = getDbGenres();
    genreRepository.saveAll(dbGenres);
  }

  @DisplayName("должен загружать жанры по ids")
  @Test
  void shouldReturnCorrectGenresByIds() {
    var expectedGenres = getDbGenres();
    List<String> ids = expectedGenres.stream()
        .map(Genre::getId)
        .toList();
    var actualGenres = genreService.findAllGenresByIds(ids);
    assertThat(actualGenres).usingRecursiveComparison()
        .isEqualTo(expectedGenres);
  }

  @DisplayName("должен загружать список всех жанров")
  @Test
  void shouldReturnCorrectGenresList() {
    var actualGenres = genreService.findAll();
    var expectedGenres = dbGenres;

    assertThat(actualGenres).usingRecursiveComparison()
        .isEqualTo(expectedGenres);
  }

  private static List<Genre> getDbGenres() {
    return IntStream.range(1, 7).boxed()
        .map(id -> new Genre(String.valueOf(id), "Genre_" + id))
        .toList();
  }
}
