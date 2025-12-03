package ru.otus.hw.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;

@WebMvcTest(controllers = AuthorController.class)
@Import(SecurityConfiguration.class)
public class AuthorControllerSecurityTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private AuthorService authorService;


  @ParameterizedTest
  @MethodSource("provideUsersWithRoles")
  void getAllAuthors_withAuthentication(String userName, String... roles) throws Exception {
    mvc.perform(get("/authors").with(user(userName).roles(roles)))
        .andExpect(status().isOk());
  }

  @Test
  void getAllAuthors_NotAuthentication() throws Exception {
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


