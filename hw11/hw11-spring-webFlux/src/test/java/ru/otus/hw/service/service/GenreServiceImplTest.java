package ru.otus.hw.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.GenreServiceImpl;

@DataMongoTest
@Import(GenreServiceImpl.class)
class GenreServiceImplTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private GenreService genreService;

  private List<Genre> dbGenres;

  @BeforeEach
  void setUp() {
    dbGenres = getDbGenres();
  }

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection(Genre.class);
  }

  @DisplayName("должен загружать жанры по ids")
  @Test
  void shouldReturnCorrectGenresByIds() {
    List<String> ids = getDbGenres().stream()
        .map(Genre::getId)
        .toList();
   List<Genre> insertGenres = mongoTemplate.insertAll(getDbGenres()).stream().toList();
    var expectedGenres = genreService.findAllGenresByIds(ids);
    assertThat(expectedGenres).usingRecursiveComparison()
        .isEqualTo(insertGenres);
  }

  @DisplayName("должен загружать список всех жанров")
  @Test
  void shouldReturnCorrectGenresList() {
    List<Genre> insertGenres = mongoTemplate.insertAll(getDbGenres()).stream().toList();
    var expectedGenres = genreService.findAll();;

    assertThat(insertGenres).usingRecursiveComparison()
        .isEqualTo(expectedGenres);
  }

  private static List<Genre> getDbGenres() {
    return IntStream.range(1, 7).boxed()
        .map(id -> new Genre(String.valueOf(id), "Genre_" + id))
        .toList();
  }
}
