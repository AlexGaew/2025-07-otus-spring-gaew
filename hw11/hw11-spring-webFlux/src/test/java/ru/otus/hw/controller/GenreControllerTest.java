package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

@WebFluxTest(GenreController.class)
public class GenreControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private GenreService genreService;

  @Test
  void shouldReturnCorrectGenres() {
    List<GenreDto> genreDtos = List.of(new GenreDto("1", "Roman"), new GenreDto("2", "Detective"));
    given(genreService.findAll()).willReturn(Flux.fromIterable(genreDtos));

    webTestClient.get()
        .uri("/api/v1/genre")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(GenreDto.class)
        .isEqualTo(genreDtos);
  }
}