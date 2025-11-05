package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;

  @Override
  public Optional<CommentDto> findCommentById(String id) {
    var comment = commentRepository.findById(id);
    return comment.map(CommentDto::from);
  }

  @Override
  public List<CommentDto> findAllById(String bookId) {
    var comments = commentRepository.findByBookId(bookId);
    return CommentDto.fromList(comments);
  }

  @Override
  public Optional<CommentDto> addComment(String bookId, String comment) {

    Comment newComment = new Comment(null, comment, bookId);
    Comment saved = commentRepository.save(newComment);
    return Optional.of(CommentDto.from(saved));
  }
}
