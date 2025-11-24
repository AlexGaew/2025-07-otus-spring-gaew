package ru.otus.hw.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CommentServiceImpl;

@DataJpaTest
@Import(CommentServiceImpl.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CommentServiceImplTest {

  @Autowired
  private CommentService commentService;

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenFindCommentById() {
    assertDoesNotThrow(() -> commentService.findCommentById(1L));
  }

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenFindAllById() {
    assertDoesNotThrow(() -> commentService.findAllById(1L));
  }

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenAddComment() {
    assertDoesNotThrow(() -> commentService.addComment(1L, "New comment"));
  }
}
