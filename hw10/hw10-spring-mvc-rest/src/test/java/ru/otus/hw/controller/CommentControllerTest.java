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
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.CommentService;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private CommentService commentService;

  @Autowired
  private ObjectMapper jacksonObjectMapper;

  @Test
  void shouldReturnCorrectCommentById() throws Exception {
    CommentDto comment = new CommentDto(1, "comment", 1);
    given(commentService.findCommentById(1)).willReturn(comment);

    mvc.perform(get("/api/v1/comment/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(comment)));
  }

  @Test
  void shouldReturnCorrectAllCommentByBookId() throws Exception {
    CommentDto comment = new CommentDto(1, "comment", 1);
    given(commentService.findAllById(1L)).willReturn(List.of(comment));

    mvc.perform(get("/api/v1/book/{id}/comments", 1))
        .andExpect(status().isOk())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(List.of(comment))));
  }

  @Test
  void shouldReturnExpectedErrorWhenCommentNotFound() throws Exception {
    given(commentService.findCommentById(1)).willThrow(NotFoundException.class);

    var msg = Map.of("error", "An unexpected error has occurred. Please try again later.");

    mvc.perform(get("/api/v1/comment/{id}", 1))
        .andExpect(status().isNotFound())
        .andExpect(content().json(jacksonObjectMapper.writeValueAsString(msg)));
  }
}
