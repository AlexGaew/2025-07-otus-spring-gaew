package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;

@Component
public class CommentConverter {

  public String commentToString(CommentDto comment) {
    return "Comment: %s".formatted(comment.comment());
  }
}


