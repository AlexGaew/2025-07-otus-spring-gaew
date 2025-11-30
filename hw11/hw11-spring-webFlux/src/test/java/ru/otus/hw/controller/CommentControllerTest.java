package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@WebFluxTest(CommentController.class)
public class CommentControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private CommentService commentService;

  @Test
  void shouldReturnCorrectCommentById() {
    CommentDto comment = new CommentDto("1", "comment", "1");
    given(commentService.findCommentById("1")).willReturn(Mono.just(comment));

    webTestClient.get()
        .uri("/api/v1/comment/{id}", "1")
        .exchange()
        .expectStatus().isOk()
        .expectBody(CommentDto.class)
        .isEqualTo(comment);
  }

  @Test
  void shouldReturnCorrectAllCommentByBookId() {
    CommentDto comment = new CommentDto("1", "comment", "1");
    given(commentService.findAllById("1")).willReturn(Flux.just(comment));

    webTestClient.get()
        .uri("/api/v1/book/{id}/comment", "1")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(CommentDto.class)
        .isEqualTo(List.of(comment));
  }

  @Test
  void shouldReturnNotFoundWhenCommentNotFound() {
    given(commentService.findCommentById("1")).willReturn(Mono.empty());

    webTestClient.get()
        .uri("/api/v1/comment/{id}", "1")
        .exchange()
        .expectStatus().isNotFound();
  }
}