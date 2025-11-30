package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;

public interface CommentService {

  Mono<CommentDto> findCommentById(String id);

  Flux<CommentDto> findAllById(String bookId);

  Mono<CommentDto> addComment(String bookId, String comment);
}
