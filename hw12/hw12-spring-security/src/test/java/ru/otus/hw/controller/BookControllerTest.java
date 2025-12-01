package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

@WebMvcTest(BookController.class)
@Import(SecurityConfiguration.class)
public class BookControllerTest {

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

  private BookDto bookDto;
  private AuthorDto authorDto;
  private GenreDto genreDto;
  private CommentDto commentDto;

  @BeforeEach
  void setUp() {
    authorDto = new AuthorDto(1L, "Peter Parker");
    genreDto = new GenreDto(1L, "Fantasy");
    bookDto = new BookDto(1L, "book1", authorDto, List.of(genreDto));
    commentDto = new CommentDto(1L, "comment1", 1L);

  }

  @ParameterizedTest
  @MethodSource("provideUsersWithRoles")
  void showAllBooks_withAuthenticatedUser_shouldSucceed(String userName, String... roles) throws Exception {
    List<BookDto> bookDtos = List.of(bookDto);

    given(bookService.findAll()).willReturn(bookDtos);

    mvc.perform(get("/books").with(user(userName).roles(roles)))
        .andExpect(status().isOk())
        .andExpect(view().name("list-books"))
        .andExpect(model().attributeExists("books"));
  }

  @Test
  void showAllBooks_withoutAuthentication_shouldRedirect() throws Exception {
    List<BookDto> bookDtos = List.of(bookDto);

    given(bookService.findAll()).willReturn(bookDtos);

    mvc.perform(get("/books"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  void showCommentsBook() throws Exception {

    given(commentService.findAllById(1L)).willReturn(List.of(commentDto));
    given(bookService.findById(1L)).willReturn(bookDto);

    mvc.perform(get("/comments-book/{bookId}", 1L))
        .andExpect(status().isOk())
        .andExpect(view().name("book-info"))
        .andExpect(model().attributeExists("book"))
        .andExpect(model().attributeExists("comments"));
  }

  @Test
  void showCommentsBookThrowExceptionWhenBookNotFound() throws Exception {

    given(commentService.findAllById(1L)).willReturn(List.of(commentDto));
    given(bookService.findById(1L)).willThrow(NotFoundException.class);

    mvc.perform(get("/comments-book/{bookId}", 1))
        .andExpect(status().isOk())
        .andExpect(view().name("not-found"));
  }

  @Test
  void showCreateForm_withAdmin_shouldSucceed() throws Exception {
    given(bookService.insert("title", 1L, List.of(1L))).willReturn(bookDto);

    mvc.perform(post("/book/create").with(user("admin").roles("ADMIN"))
            .param("title", "title")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/books"));
  }

  @Test
  void showCreateForm_withoutAuth_shouldRedirect() throws Exception {
    given(bookService.insert("title", 1L, List.of(1L))).willReturn(bookDto);

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
    given(bookService.insert("title", 1L, List.of(1L))).willReturn(bookDto);

    mvc.perform(post("/book/create").with(user("user").roles("USER"))
            .param("title", "title")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().isForbidden());
  }

  @Test
  void showUpdateForm_withAdmin_shouldSucceed() throws Exception {

    given(bookService.update(1L, "book_1", 1L, List.of(1L))).willReturn(bookDto);

    mvc.perform(post("/book/{id}/edit", 1L).with(user("admin").roles("ADMIN"))
            .param("title", "book_1")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/books"));
  }

  @Test
  void showUpdateForm_withUser_shouldForbidden() throws Exception {

    given(bookService.update(1L, "book_1", 1L, List.of(1L))).willReturn(bookDto);

    mvc.perform(post("/book/{id}/edit", 1L).with(user("user").roles("User"))
            .param("title", "book_1")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().isForbidden());
  }

  @Test
  void showUpdateForm_withoutAuth_shouldRedirect() throws Exception {

    given(bookService.update(1L, "book_1", 1L, List.of(1L))).willReturn(bookDto);

    mvc.perform(post("/book/{id}/edit", 1L)
            .param("title", "book_1")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  void deleteBook_withAdmin_shouldSucceed() throws Exception {

    willDoNothing().given(bookService).deleteById(1L);

    mvc.perform(post("/book/{id}/delete", 1L).with(user("admin").roles("ADMIN"))
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/books"));
  }

  @Test
  void deleteBook_withUser_shouldForbidden() throws Exception {

    willDoNothing().given(bookService).deleteById(1L);

    mvc.perform(post("/book/{id}/delete", 1L).with(user("user").roles("USER"))
        )
        .andExpect(status().isForbidden());
  }

  @Test
  void deleteBook_withoutAuth_shouldRedirect() throws Exception {

    willDoNothing().given(bookService).deleteById(1L);

    mvc.perform(post("/book/{id}/delete", 1L)
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  void showEditForm_withAdmin_shouldSucceed() throws Exception {

    given(bookService.findById(1L)).willReturn(bookDto);
    given(authorService.findAll()).willReturn(List.of(authorDto));
    given(genreService.findAll()).willReturn(List.of(genreDto));

    mvc.perform(get("/book/{id}/edit", 1L).with(user("user").roles("USER")))
        .andExpect(status().isForbidden());

  }

  @Test
  void showEditForm_withoutAuth_shouldRedirect() throws Exception {

    given(bookService.findById(1L)).willReturn(bookDto);
    given(authorService.findAll()).willReturn(List.of(authorDto));
    given(genreService.findAll()).willReturn(List.of(genreDto));

    mvc.perform(get("/book/{id}/edit", 1L))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));

  }

  @Test
  void showEditForm_withUser_shouldForbidden() throws Exception {

    given(bookService.findById(1L)).willReturn(bookDto);
    given(authorService.findAll()).willReturn(List.of(authorDto));
    given(genreService.findAll()).willReturn(List.of(genreDto));

    mvc.perform(get("/book/{id}/edit", 1L).with(user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(view().name("edit-book"))
        .andExpect(model().attributeExists("bookRequest"))
        .andExpect(model().attributeExists("authors"))
        .andExpect(model().attributeExists("genres"));
  }

  @Test
  void showEditFormWillThrowException() throws Exception {

    given(bookService.findById(1L)).willThrow(NotFoundException.class);

    mvc.perform(get("/comments-book/{bookId}", 1))
        .andExpect(status().isOk())
        .andExpect(view().name("not-found"));
  }

  private static Stream<Arguments> provideUsersWithRoles() {
    return Stream.of(
        Arguments.of("user", new String[]{"USER"}),
        Arguments.of("admin", new String[]{"ADMIN"})
    );
  }
}
