package ru.otus.hw.dto;

import java.util.List;
import ru.otus.hw.models.Comment;

public record CommentDto(String id, String comment, String bookId) {

  public static CommentDto from(Comment comment) {
    return new CommentDto(comment.getId(), comment.getComment(), comment.getBookId());
  }

  public static List<CommentDto> fromList(List<Comment> comments) {
    return comments.stream()
        .map(CommentDto::from)
        .toList();
  }
}
