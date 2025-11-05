package ru.otus.hw.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CommentServiceImpl;

@DataMongoTest
@Import({CommentServiceImpl.class, BookServiceImpl.class})
public class CommentServiceImplTest {

  @Autowired
  private CommentService commentService;

  @Autowired
  private BookService bookService;

  @Test
  void shouldFindCommentById() {
    BookDto bookDto1 = bookService.insert("title_1", "Author1", List.of(new Genre("Genre1")));

    var savedComment = commentService.addComment(bookDto1.id(), "Comment1");
    var foundComment = commentService.findCommentById(savedComment.get().id());

    assertThat(foundComment).isPresent()
        .get()
        .usingRecursiveComparison()
        .isEqualTo(savedComment.get());

  }

  @Test
  void shouldFindAllCommentsByBookId() {
    BookDto bookDto1 = bookService.insert("title_1", "Author1", List.of(new Genre("Genre1")));

    var savedComment = commentService.addComment(bookDto1.id(), "Comment1");
    var savedComment2 = commentService.addComment(bookDto1.id(), "Comment2");
    List<CommentDto> comments = List.of(savedComment.get(), savedComment2.get());
    var foundComments = commentService.findAllById(savedComment.get().bookId());

    assertThat(foundComments)
        .usingRecursiveComparison()
        .isEqualTo(comments);
  }

  @Test
  void shouldAddCommentByBookId() {
    BookDto bookDto1 = bookService.insert("title_1", "Author1", List.of(new Genre("Genre1")));

    var savedComment = commentService.addComment(bookDto1.id(), "Comment1");
    var foundComments = commentService.findCommentById(savedComment.get().id());

    assertThat(savedComment.get().comment())
        .isEqualTo(foundComments.get().comment());
  }
}
