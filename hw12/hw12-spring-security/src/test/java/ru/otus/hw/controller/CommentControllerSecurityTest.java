package ru.otus.hw.controller;

import static java.util.Objects.nonNull;
import static org.mockito.BDDMockito.given;
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
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.CommentService;

@WebMvcTest(CommentController.class)
@Import(SecurityConfiguration.class)
public class CommentControllerSecurityTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private CommentService commentService;


  @ParameterizedTest(name = "{0} {1} for user {2} should return {4} status")
  @MethodSource("getTestData")
  void shouldReturnExpectedStatus(String method, String url, String userName, String[] roles,
      int status, boolean checkLoginDirection) throws Exception {
    CommentDto comment = new CommentDto(1, "comment", 1);
    given(commentService.findCommentById(1)).willReturn(comment);

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

  private static Stream<Arguments> getTestData() {
    var roles = new String[]{"USER", "ADMIN"};
    return Stream.of(
        Arguments.of("get", "/comment/1", "user", roles, 200, false),
        Arguments.of("get", "/comment/1", "admin", roles, 200, false)
    );
  }
}
