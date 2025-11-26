package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;

  private final BookRepository bookRepository;

  @Override
  public Mono<CommentDto> findCommentById(String id) {
    var comment = commentRepository.findById(id);
    return comment.map(CommentDto::from);
  }

  @Override
  public Flux<CommentDto> findAllById(String bookId) {
    var comments = commentRepository.findByBookId(bookId);
    return comments.map(CommentDto::from);
  }

  @Override
  public Mono<CommentDto> addComment(String bookId, String comment) {
    return bookRepository.findById(bookId)
        .switchIfEmpty(Mono.error(new EntityNotFoundException("Book not found: " + bookId)))
        .flatMap(book -> commentRepository.save(new Comment(null, comment, bookId)))
        .map(CommentDto::from);
  }
}
