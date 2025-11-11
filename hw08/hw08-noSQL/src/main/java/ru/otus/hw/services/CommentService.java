package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;
import ru.otus.hw.dto.CommentDto;

public interface CommentService {

  Optional<CommentDto> findCommentById(String id);

  List<CommentDto> findAllById(String bookId);

  Optional<CommentDto> addComment(String bookId, String comment);
}
