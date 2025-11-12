package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private AuthorService authorService;

  @Test
  void getAllAuthors() throws Exception {
    List<AuthorDto> authorDtos = List.of(new AuthorDto(1L, "Pushkin"), new AuthorDto(2L, "Mike"));
    given(authorService.findAll()).willReturn(authorDtos);

    mvc.perform(get("/authors"))
        .andExpect(status().isOk())
        .andExpect(view().name("list-authors"))
        .andExpect(model().attributeExists("authors"));
  }
}
