package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;
import ru.otus.hw.dto.CommentDto;

public interface CommentService {

  Optional<CommentDto> findCommentById(long id);

  List<CommentDto> findAllById(Long bookId);

  Optional<CommentDto> addComment(long bookId, String comment);

}
