package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;

@WebMvcTest(BookController.class)
public class BookControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private BookService bookService;

  @Autowired
  private ObjectMapper jacksonObjectMapper;

  @Test
  void shouldReturnCorrectBooksList() throws Exception {
    AuthorDto authorDto = new AuthorDto(1L, "Pushkin");
    GenreDto genreDto = new GenreDto(1L, "Roman");
    BookDto bookDto = new BookDto(1L, "Book1", authorDto, List.of(genreDto));
    List<BookDto> books = List.of(bookDto);

    given(bookService.findAll()).willReturn(books);

    mvc.perform(get("/api/v1/book"))
        .andExpect(status().isOk())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(books)));
  }

  @Test
  void shouldReturnCorrectBookById() throws Exception {
    AuthorDto authorDto = new AuthorDto(1L, "Pushkin");
    GenreDto genreDto = new GenreDto(1L, "Roman");
    BookDto bookDto = new BookDto(1L, "Book1", authorDto, List.of(genreDto));

    given(bookService.findById(1L)).willReturn(bookDto);

    mvc.perform(get("/api/v1/book/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(bookDto)));
  }

  @Test
  void shouldReturnExpectedErrorWhenBookNotFound() throws Exception {
    given(bookService.findById(1L)).willThrow(EntityNotFoundException.class);

    var msg = Map.of("error", "An unexpected error has occurred. Please try again later.");

    mvc.perform(get("/api/v1/book/{id}", 1))
        .andExpect(status().isNotFound())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(msg)));
  }

  @Test
  void shouldCreateBook() throws Exception {
    AuthorDto authorDto = new AuthorDto(1L, "Pushkin");
    GenreDto genreDto = new GenreDto(1L, "Roman");
    BookDto bookDto = new BookDto(1L, "Book1", authorDto, List.of(genreDto));

    given(bookService.insert("Book1", 1L, List.of(1L))).willReturn(bookDto);

    BookRequestDto request = new BookRequestDto();
    request.setTitle("Book1");
    request.setAuthorId(1L);
    request.setGenreIds(List.of(1L));

    mvc.perform(post("/api/v1/book")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(bookDto)));
  }

  @Test
  void shouldUpdateBook() throws Exception {
    AuthorDto authorDto = new AuthorDto(1L, "Pushkin");
    GenreDto genreDto = new GenreDto(1L, "Roman");
    BookDto bookDto = new BookDto(1L, "Updated", authorDto, List.of(genreDto));

    given(bookService.update(1L, "Updated", 1L, List.of(1L))).willReturn(bookDto);

    BookRequestDto request = new BookRequestDto();
    request.setTitle("Updated");
    request.setAuthorId(1L);
    request.setGenreIds(List.of(1L));

    mvc.perform(patch("/api/v1/book/{id}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(bookDto)));
  }

  @Test
  void shouldDeleteBook() throws Exception {
    willDoNothing().given(bookService).deleteById(1L);

    mvc.perform(delete("/api/v1/book/{id}", 1))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturnExpectedErrorWhenDeleteBookNotFound() throws Exception {
    given(bookService.findById(1L)).willThrow(EntityNotFoundException.class);

    var msg = Map.of("error", "An unexpected error has occurred. Please try again later.");

    mvc.perform(get("/api/v1/book/{id}", 1))
        .andExpect(status().isNotFound())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(msg)));
  }
}