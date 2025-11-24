package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.GenreService;

@WebMvcTest(GenreController.class)
public class GenreControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private GenreService genreService;

  @Autowired
  private ObjectMapper jacksonObjectMapper;


  @Test
  void shouldReturnCorrectGenres() throws Exception {
    List<GenreDto> genreDtos = List.of(new GenreDto(1L, "Roman"), new GenreDto(2L, "Detective"));
    given(genreService.findAll()).willReturn(genreDtos);

    mvc.perform(get("/api/v1/genre"))
        .andExpect(status().isOk())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(genreDtos)));
  }

  @Test
  void shouldReturnExpectedErrorWhenGenresNotFound() throws Exception {
    given(genreService.findAll()).willThrow(EntityNotFoundException.class);

    var msg = Map.of("error", "An unexpected error has occurred. Please try again later.");

    mvc.perform(get("/api/v1/genre"))
        .andExpect(status().isNotFound())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(msg)));
  }
}
