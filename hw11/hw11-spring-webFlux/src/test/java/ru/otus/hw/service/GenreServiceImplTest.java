package ru.otus.hw.service;


import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.GenreServiceImpl;

@DataMongoTest
@Import(GenreServiceImpl.class)
class GenreServiceImplTest {

  @Autowired
  private ReactiveMongoTemplate mongoTemplate;

  @Autowired
  private GenreService genreService;

  private List<Genre> dbGenres;

  @BeforeEach
  void setUp() {
    dbGenres = getDbGenres();
  }

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection(Genre.class).block();
  }

  @DisplayName("должен загружать жанры по ids")
  @Test
  void shouldReturnCorrectGenresByIds() {
    List<Genre> insertGenres = mongoTemplate.insertAll(getDbGenres()).collectList().block();
    List<String> ids = insertGenres.stream()
        .map(Genre::getId)
        .toList();
    var expectedGenres = insertGenres.stream()
        .map(GenreDto::from)
        .toList();

    StepVerifier.create(genreService.findAllGenresByIds(ids))
        .expectNextSequence(expectedGenres)
        .verifyComplete();
  }

  @DisplayName("должен загружать список всех жанров")
  @Test
  void shouldReturnCorrectGenresList() {
    List<Genre> insertGenres = mongoTemplate.insertAll(getDbGenres()).collectList().block();
    var expectedGenres = insertGenres.stream()
        .map(GenreDto::from)
        .toList();

    StepVerifier.create(genreService.findAll())
        .expectNextSequence(expectedGenres)
        .verifyComplete();
  }

  private static List<Genre> getDbGenres() {
    return IntStream.range(1, 7).boxed()
        .map(id -> new Genre(String.valueOf(id), "Genre_" + id))
        .toList();
  }
}
