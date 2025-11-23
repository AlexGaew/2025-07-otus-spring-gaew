package ru.otus.hw.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @GetMapping("/comment/{id}")
  public ResponseEntity<CommentDto> getComment(@PathVariable("id") long id) {
    CommentDto comment = commentService.findCommentById(id);
    return ResponseEntity.ok(comment);
  }

  @GetMapping("/book/{id}/comments")
  public List<CommentDto> getBookComments(@PathVariable("id") long id) {
    return commentService.findAllById(id);
  }
}
