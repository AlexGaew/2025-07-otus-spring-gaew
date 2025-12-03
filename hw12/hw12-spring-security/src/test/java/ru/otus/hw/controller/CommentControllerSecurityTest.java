package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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

  @Test
  void getCommentByIdAnyUsers() throws Exception {
    CommentDto comment = new CommentDto(1, "comment", 1);
    given(commentService.findCommentById(1)).willReturn(comment);

    mvc.perform(get("/comment/{id}", 1))
        .andExpect(status().isOk());
  }
}
