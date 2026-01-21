package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;

  private final BookRepository bookRepository;

  @CircuitBreaker(
      name = "commentService",
      fallbackMethod = "findCommentByIdFallback"
  )
  @Retry(name = "commentService")
  @Transactional(readOnly = true)
  public CommentDto findCommentById(long id) {
    var comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment not found"));
    return CommentDto.from(comment);
  }

  public CommentDto findCommentByIdFallback(long id, Throwable ex) {
    log.warn("CircuitBreaker fallback: findCommentById, id={}", id, ex);
    throw new RuntimeException("Temporary error while loading comment");
  }

  @CircuitBreaker(
      name = "commentService",
      fallbackMethod = "findAllCommentsByBookIdFallback"
  )
  @Transactional(readOnly = true)
  public List<CommentDto> findAllById(Long bookId) {
    var comments = commentRepository.findByBookId(bookId);

    if (comments.isEmpty()) {
      throw new NotFoundException("Comment not found");
    }

    return CommentDto.fromList(comments);
  }

  public List<CommentDto> findAllCommentsByBookIdFallback(Long bookId, Throwable ex) {
    log.warn("CircuitBreaker fallback: findAllCommentsByBookId, bookId={}", bookId, ex);
    throw new RuntimeException("Temporary error while loading comments");
  }

  @CircuitBreaker(
      name = "commentService",
      fallbackMethod = "addCommentFallback"
  )
  @Retry(name = "commentService")
  @Transactional
  public Optional<CommentDto> addComment(long bookId, String comment) {
    var book = bookRepository.findById(bookId)
        .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    Comment newComment = new Comment(0, comment, book);
    Comment saved = commentRepository.save(newComment);
    return Optional.of(CommentDto.from(saved));
  }

  public Optional<CommentDto> addCommentFallback(long bookId, String comment, Throwable ex) {
    log.warn("CircuitBreaker fallback: addComment, bookId={}, comment={}", bookId, comment, ex);
    throw new RuntimeException("Temporary error while adding comment");
  }
}
