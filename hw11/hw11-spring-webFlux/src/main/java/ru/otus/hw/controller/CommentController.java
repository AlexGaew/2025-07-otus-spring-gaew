package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @GetMapping("/comment/{id}")
  public Mono<ResponseEntity<CommentDto>> getComment(@PathVariable("id") String id) {
    var comment = commentService.findCommentById(id);
    return comment
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping("/book/{id}/comment")
  public Flux<CommentDto> getBookComments(@PathVariable("id") String id) {
    return commentService.findAllById(id);
  }
}
