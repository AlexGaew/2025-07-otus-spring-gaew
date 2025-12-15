package ru.otus.hw.controller;

import static java.util.Objects.nonNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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


  @ParameterizedTest(name = "{0} {1} for user {2} should return {4} status")
  @MethodSource("getTestDataBooks")
  void shouldReturnExpectedStatusUrlBooks(String method, String url, String userName, String[] roles,
      int status, boolean checkLoginDirection) throws Exception {

    var request = method2RequestBuilder(method, url);

    if (nonNull(userName)) {
      request = request.with(user(userName).roles(roles));
    }
    ResultActions resultActions = mvc.perform(request).andExpect(status().is(status));

    if (checkLoginDirection) {
      resultActions.andExpect(redirectedUrlPattern("**/login"));
    }

  }

  @ParameterizedTest(name = "{0} {1} for user {2} should return {4} status")
  @MethodSource("getTestDataBooksEdit")
  void shouldReturnExpectedStatusUrlBooksEdit(String method, String url, String userName, String[] roles,
      int status, boolean checkLoginDirection) throws Exception {

    var request = method2RequestBuilder(method, url);

    if (nonNull(userName)) {
      request = request.with(user(userName).roles(roles));
    }
    ResultActions resultActions = mvc.perform(request).andExpect(status().is(status));

    if (checkLoginDirection) {
      resultActions.andExpect(redirectedUrlPattern("**/login"));
    }
  }

  @ParameterizedTest(name = "{0} {1} for user {2} should return {4} status")
  @MethodSource("getTestDataBooksDelete")
  void shouldReturnExpectedStatusUrlBooksDelete(String method, String url, String userName, String[] roles,
      int status, boolean checkLoginDirection) throws Exception {

    var request = method2RequestBuilder(method, url);

    if (nonNull(userName)) {
      request = request.with(user(userName).roles(roles));
    }
    ResultActions resultActions = mvc.perform(request).andExpect(status().is(status));

    if (checkLoginDirection) {
      resultActions.andExpect(redirectedUrlPattern("**/login"));
    }
  }

  @ParameterizedTest(name = "{0} {1} for user {2} should return {4} status")
  @MethodSource("getTestDataBooksCreate")
  void shouldReturnExpectedStatusUrlBooksCreate(String method, String url, String userName, String[] roles,
      int status, boolean checkLoginDirection) throws Exception {

    var request = method2RequestBuilder(method, url)
        .param("title", "title")
        .param("authorId", "1")
        .param("genreIds", "1");

    if (nonNull(userName)) {
      request = request.with(user(userName).roles(roles));
    }
    ResultActions resultActions = mvc.perform(request).andExpect(status().is(status));

    if (checkLoginDirection) {
      resultActions.andExpect(redirectedUrlPattern("**/login"));
    }
  }

  @ParameterizedTest(name = "{0} {1} for user {2} should return {4} status")
  @MethodSource("getTestDataBookComments")
  void shouldReturnExpectedStatusUrlBookComments(String method, String url, String userName, String[] roles,
      int status, boolean checkLoginDirection) throws Exception {

    var request = method2RequestBuilder(method, url);

    if (nonNull(userName)) {
      request = request.with(user(userName).roles(roles));
    }
    ResultActions resultActions = mvc.perform(request).andExpect(status().is(status));

    if (checkLoginDirection) {
      resultActions.andExpect(redirectedUrlPattern("**/login"));
    }
  }


  private MockHttpServletRequestBuilder method2RequestBuilder(String method, String url) {
    Map<String, Function<String, MockHttpServletRequestBuilder>> methodMap =
        Map.of("get", MockMvcRequestBuilders::get, "post", MockMvcRequestBuilders::post);
    return methodMap.get(method).apply(url);
  }

  private static Stream<Arguments> getTestDataBooks() {
    var roles = new String[]{"USER", "ADMIN", "MANAGER"};
    return Stream.of(
        Arguments.of("get", "/books", "user", roles, 200, false),
        Arguments.of("get", "/books", "admin", roles, 200, false),
        Arguments.of("get", "/books", null, null, 302, true)
    );
  }

  private static Stream<Arguments> getTestDataBooksEdit() {
    var roles = new String[]{"USER", "ADMIN", "MANAGER"};
    return Stream.of(
        Arguments.of("get", "/book/1/edit", "user", new String[]{"USER"}, 403, false),
        Arguments.of("get", "/book/1/edit", "admin", roles, 200, false),
        Arguments.of("get", "/book/1/edit", null, null, 302, true),
        Arguments.of("post", "/book/1/edit", "user", new String[]{"USER"}, 403, false),
        Arguments.of("post", "/book/1/edit", "admin", roles, 200, false),
        Arguments.of("post", "/book/1/edit", null, null, 302, true)
    );
  }

  private static Stream<Arguments> getTestDataBooksDelete() {
    var roles = new String[]{"USER", "ADMIN", "MANAGER"};
    return Stream.of(
        Arguments.of("post", "/book/1/delete", "user", new String[]{"USER"}, 403, false),
        Arguments.of("post", "/book/1/delete", "manager", new String[]{"MANAGER"}, 403, false),
        Arguments.of("post", "/book/1/delete", "admin", roles, 302, false),
        Arguments.of("post", "/book/1/delete", null, null, 302, true)
    );
  }

  private static Stream<Arguments> getTestDataBooksCreate() {
    var roles = new String[]{"USER", "ADMIN", "MANAGER"};
    return Stream.of(
        Arguments.of("post", "/book/create", "user", new String[]{"USER"}, 403, false),
        Arguments.of("post", "/book/create", "admin", roles, 302, false),
        Arguments.of("post", "/book/create", null, null, 302, true)
    );
  }

  private static Stream<Arguments> getTestDataBookComments() {
    var roles = new String[]{"USER", "ADMIN", "MANAGER"};
    var urls = new String[]{"/comments-book/1", "ADMIN"};
    return Stream.of(
        Arguments.of("post", "/comments-book/1", "user", roles, 200, false),
        Arguments.of("post", "/comments-book/1", null, null, 200, false)
    );
  }
}
