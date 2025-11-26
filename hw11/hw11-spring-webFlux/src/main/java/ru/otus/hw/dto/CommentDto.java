package ru.otus.hw.dto;

import ru.otus.hw.models.Comment;

public record CommentDto(String id, String comment, String bookId) {

  public static CommentDto from(Comment comment) {
    return new CommentDto(comment.getId(), comment.getComment(), comment.getBook().getId());
  }
}
