package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

@WebMvcTest(GenreController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GenreControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private GenreService genreService;

  @Test
  void getAllGenres() throws Exception {
    List<GenreDto> genreDtos = List.of(new GenreDto(1L, "Roman"), new GenreDto(2L, "Detective"));
    given(genreService.findAll()).willReturn(genreDtos);

    mvc.perform(get("/genres"))
        .andExpect(status().isOk())
        .andExpect(view().name("list-genres"))
        .andExpect(model().attributeExists("genres"));
  }
}
