package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

@WebFluxTest(AuthorController.class)
public class AuthorControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private AuthorService authorService;

  @Test
  void shouldReturnCorrectAuthorsList() {
    List<AuthorDto> authors = List.of(new AuthorDto("1", "Pushkin"), new AuthorDto("2", "Mike"));
    given(authorService.findAll()).willReturn(Flux.fromIterable(authors));

    webTestClient.get()
        .uri("/api/v1/author")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(AuthorDto.class)
        .isEqualTo(authors);
  }
}
