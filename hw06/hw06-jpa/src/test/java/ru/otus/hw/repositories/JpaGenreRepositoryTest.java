//package ru.otus.hw.repositories;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.context.annotation.Import;
//import ru.otus.hw.models.Genre;
//
//@DisplayName("Репозиторий на основе Jdbc для работы с Жанрами ")
//@JdbcTest
//@Import({JpaGenreRepository.class})
//class JpaGenreRepositoryTest {
//
//  @Autowired
//  private JpaGenreRepository repositoryJdbc;
//
//  private List<Genre> dbGenres;
//
//  @BeforeEach
//  void setUp() {
//    dbGenres = getDbGenres();
//  }
//
//  @DisplayName("должен загружать жанры по ids")
//  @Test
//  void shouldReturnCorrectGenresByIds() {
//    var expectedGenres = getDbGenres();
//    Set<Long> ids = expectedGenres.stream()
//        .map(Genre::getId)
//        .collect(Collectors.toSet());
//    var actualGenres = repositoryJdbc.findAllByIds(ids);
//    assertThat(actualGenres).isEqualTo(expectedGenres);
//    actualGenres.forEach(System.out::println);
//  }
//
//  @DisplayName("должен загружать список всех жанров")
//  @Test
//  void shouldReturnCorrectGenresList() {
//    var actualGenres = repositoryJdbc.findAll();
//    var expectedGenres = dbGenres;
//
//    assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
//    actualGenres.forEach(System.out::println);
//  }
//
//  private static List<Genre> getDbGenres() {
//    return IntStream.range(1, 7).boxed()
//        .map(id -> new Genre(id, "Genre_" + id))
//        .toList();
//  }
//}