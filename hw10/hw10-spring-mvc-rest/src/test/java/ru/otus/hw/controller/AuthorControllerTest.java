package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;

@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private AuthorService authorService;
  @Autowired
  private ObjectMapper jacksonObjectMapper;

  @Test
  void shouldReturnCorrectAuthorsList() throws Exception {
    List<AuthorDto> authors = List.of(new AuthorDto(1L, "Pushkin"), new AuthorDto(2L, "Mike"));
    given(authorService.findAll()).willReturn(authors);

    mvc.perform(get("/api/v1/author"))
        .andExpect(status().isOk())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(authors)));
  }

  @Test
  void shouldReturnExpectedErrorWhenAuthorsNotFound() throws Exception {
    given(authorService.findAll()).willThrow(EntityNotFoundException.class);

    var msg = Map.of("error", "An unexpected error has occurred. Please try again later.");

    mvc.perform(get("/api/v1/author"))
        .andExpect(status().isNotFound())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(msg)));
  }
}
