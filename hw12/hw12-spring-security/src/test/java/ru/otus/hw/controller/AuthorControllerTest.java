package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;

@WebMvcTest(AuthorController.class)
@Import(SecurityConfiguration.class)
public class AuthorControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private AuthorService authorService;


  @ParameterizedTest
  @MethodSource("provideUsersWithRoles")
  void getAllAuthors_withAuthentication(String userName, String... roles) throws Exception {
    List<AuthorDto> authorDtos = List.of(new AuthorDto(1L, "Pushkin"), new AuthorDto(2L, "Mike"));
    given(authorService.findAll()).willReturn(authorDtos);

    mvc.perform(get("/authors").with(user(userName).roles(roles)))
        .andExpect(status().isOk())
        .andExpect(view().name("list-authors"))
        .andExpect(model().attributeExists("authors"));
  }

  @Test
  void getAllAuthors_NotAuthentication() throws Exception {
    List<AuthorDto> authorDtos = List.of(new AuthorDto(1L, "Pushkin"), new AuthorDto(2L, "Mike"));
    given(authorService.findAll()).willReturn(authorDtos);

    mvc.perform(get("/authors"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  private static Stream<Arguments> provideUsersWithRoles() {
    return Stream.of(
        Arguments.of("user", new String[]{"USER"}),
        Arguments.of("admin", new String[]{"ADMIN"})
    );
  }
}
