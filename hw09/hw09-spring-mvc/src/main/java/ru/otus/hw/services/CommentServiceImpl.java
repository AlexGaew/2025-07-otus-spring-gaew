package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;

  private final BookRepository bookRepository;

  @Override
  @Transactional(readOnly = true)
  public CommentDto findCommentById(long id) {
    var comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment not found"));
    return CommentDto.from(comment);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CommentDto> findAllById(Long bookId) {
    var comments = commentRepository.findByBookId(bookId);
    return CommentDto.fromList(comments);
  }

  @Override
  @Transactional
  public Optional<CommentDto> addComment(long bookId, String comment) {
    var book = bookRepository.findById(bookId)
        .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    Comment newComment = new Comment(0, comment, book);
    Comment saved = commentRepository.save(newComment);
    return Optional.of(CommentDto.from(saved));
  }
}
