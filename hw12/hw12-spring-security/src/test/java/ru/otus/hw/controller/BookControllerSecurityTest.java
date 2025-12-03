package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
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
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

@WebMvcTest(BookController.class)
@Import(SecurityConfiguration.class)
public class BookControllerSecurityTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private BookService bookService;
  @MockitoBean
  private AuthorService authorService;
  @MockitoBean
  private GenreService genreService;
  @MockitoBean
  private CommentService commentService;

  private AuthorDto authorDto;
  private GenreDto genreDto;

  @BeforeEach
  void setUp() {
    authorDto = new AuthorDto(1L, "Peter Parker");
    genreDto = new GenreDto(1L, "Fantasy");

  }

  @ParameterizedTest
  @MethodSource("provideUsersWithRoles")
  void showAllBooks_withAuthenticatedUser_shouldSucceed(String userName, String... roles) throws Exception {
    mvc.perform(get("/books").with(user(userName).roles(roles)))
        .andExpect(status().isOk());
  }

  @Test
  void showAllBooks_withoutAuthentication_shouldRedirect() throws Exception {
    mvc.perform(get("/books"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  void showCreateForm_withAdmin_shouldSucceed() throws Exception {
    mvc.perform(post("/book/create").with(user("admin").roles("ADMIN"))
            .param("title", "title")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void showCreateForm_withoutAuth_shouldRedirect() throws Exception {
    mvc.perform(post("/book/create")
            .param("title", "title")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  void showCreateForm_withUser_shouldForbidden() throws Exception {
    mvc.perform(post("/book/create").with(user("user").roles("USER"))
            .param("title", "title")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().isForbidden());
  }

  @Test
  void showUpdateForm_withAdmin_shouldSucceed() throws Exception {
    mvc.perform(post("/book/{id}/edit", 1L).with(user("admin").roles("ADMIN"))
            .param("title", "book_1")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void showUpdateForm_withUser_shouldForbidden() throws Exception {
    mvc.perform(post("/book/{id}/edit", 1L).with(user("user").roles("User"))
            .param("title", "book_1")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().isForbidden());
  }

  @Test
  void showUpdateForm_withoutAuth_shouldRedirect() throws Exception {
    mvc.perform(post("/book/{id}/edit", 1L)
            .param("title", "book_1")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void deleteBook_withAdmin_shouldSucceed() throws Exception {
    mvc.perform(post("/book/{id}/delete", 1L).with(user("admin").roles("ADMIN"))
        )
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void deleteBook_withUser_shouldForbidden() throws Exception {
    mvc.perform(post("/book/{id}/delete", 1L).with(user("user").roles("USER"))
        )
        .andExpect(status().isForbidden());
  }

  @Test
  void deleteBook_withoutAuth_shouldRedirect() throws Exception {
    mvc.perform(post("/book/{id}/delete", 1L)
        )
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void showEditForm_withAdmin_shouldSucceed() throws Exception {
    mvc.perform(get("/book/{id}/edit", 1L).with(user("user").roles("USER")))
        .andExpect(status().isForbidden());
  }

  @Test
  void showEditForm_withoutAuth_shouldRedirect() throws Exception {
    mvc.perform(get("/book/{id}/edit", 1L))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void showEditForm_withUser_shouldForbidden() throws Exception {
    mvc.perform(get("/book/{id}/edit", 1L).with(user("admin").roles("ADMIN")))
        .andExpect(status().isOk());
  }

  @Test
  void showEditFormWillThrowException() throws Exception {
    given(bookService.findById(1L)).willThrow(NotFoundException.class);

    mvc.perform(get("/comments-book/{bookId}", 1))
        .andExpect(status().isOk());
  }

  private static Stream<Arguments> provideUsersWithRoles() {
    return Stream.of(
        Arguments.of("user", new String[]{"USER"}),
        Arguments.of("admin", new String[]{"ADMIN"})
    );
  }
}
