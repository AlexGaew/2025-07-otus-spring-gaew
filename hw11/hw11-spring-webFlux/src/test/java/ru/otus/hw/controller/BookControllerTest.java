package ru.otus.hw.controller;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

@WebFluxTest(BookController.class)
public class BookControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private BookService bookService;

  @Test
  void shouldReturnCorrectBooksList() {
    AuthorDto authorDto = new AuthorDto("1", "Pushkin");
    GenreDto genreDto = new GenreDto("1", "Roman");
    BookDto bookDto = new BookDto("1", "Book1", authorDto, List.of(genreDto));
    List<BookDto> books = List.of(bookDto);

    given(bookService.findAll()).willReturn(Flux.fromIterable(books));

    webTestClient.get()
        .uri("/api/v1/book")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(BookDto.class)
        .isEqualTo(books);
  }

  @Test
  void shouldReturnCorrectBookById() {
    AuthorDto authorDto = new AuthorDto("1", "Pushkin");
    GenreDto genreDto = new GenreDto("1", "Roman");
    BookDto bookDto = new BookDto("1", "Book1", authorDto, List.of(genreDto));

    given(bookService.findById("1")).willReturn(Mono.just(bookDto));

    webTestClient.get()
        .uri("/api/v1/book/{id}", "1")
        .exchange()
        .expectStatus().isOk()
        .expectBody(BookDto.class)
        .isEqualTo(bookDto);
  }

  @Test
  void shouldReturnNotFoundWhenBookNotFound() {
    given(bookService.findById("1")).willReturn(Mono.empty());

    webTestClient.get()
        .uri("/api/v1/book/{id}", "1")
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void shouldCreateBook() {
    AuthorDto authorDto = new AuthorDto("1", "Pushkin");
    GenreDto genreDto = new GenreDto("1", "Roman");
    BookDto bookDto = new BookDto("1", "Book1", authorDto, List.of(genreDto));

    given(bookService.insert(eq("Book1"), eq("1"), anyList())).willReturn(Mono.just(bookDto));

    BookRequestDto request = new BookRequestDto();
    request.setTitle("Book1");
    request.setAuthorId("1");
    request.setGenreIds(List.of("1"));

    webTestClient.post()
        .uri("/api/v1/book")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(BookDto.class)
        .isEqualTo(bookDto);
  }

  @Test
  void shouldUpdateBook() {
    AuthorDto authorDto = new AuthorDto("1", "Pushkin");
    GenreDto genreDto = new GenreDto("1", "Roman");
    BookDto bookDto = new BookDto("1", "Updated", authorDto, List.of(genreDto));

    given(bookService.update(eq("1"), eq("Updated"), eq("1"), anyList()))
        .willReturn(Mono.just(bookDto));

    BookRequestDto request = new BookRequestDto();
    request.setTitle("Updated");
    request.setAuthorId("1");
    request.setGenreIds(List.of("1"));

    webTestClient.patch()
        .uri("/api/v1/book/{id}", "1")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isCreated();
  }

  @Test
  void shouldDeleteBook() {
    given(bookService.deleteById("1")).willReturn(Mono.empty());

    webTestClient.delete()
        .uri("/api/v1/book/{id}", "1")
        .exchange()
        .expectStatus().isNoContent();
  }
}