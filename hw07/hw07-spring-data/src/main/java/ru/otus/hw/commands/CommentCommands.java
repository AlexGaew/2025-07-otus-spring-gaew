package ru.otus.hw.commands;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

  private final CommentService commentService;

  private final CommentConverter commentConverter;

  @ShellMethod(value = "Find all comments by id books", key = "fcbid")
  public String findAllComments(long id) {
    return commentService.findAllById(id).stream()
        .map(commentConverter::commentToString)
        .collect(Collectors.joining("," + System.lineSeparator()));
  }

  @ShellMethod(value = "Find comment by id", key = "cid")
  public String findCommentById(long id) {
    return commentService.findCommentById(id)
        .map(commentConverter::commentToString)
        .orElse("Comment with id %d not found".formatted(id));
  }

  @ShellMethod(value = "Add comment by book id", key = "acbid")
  public String addCommentByBookId(long id, String comment) {
    return commentService.addComment(id, comment)
        .map(commentConverter::commentToString)
        .orElse("Book with id %d not found".formatted(id));
  }
}
