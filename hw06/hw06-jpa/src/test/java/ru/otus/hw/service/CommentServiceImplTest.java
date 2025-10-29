package ru.otus.hw.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CommentServiceImpl;

@DataJpaTest
@Import({
    CommentServiceImpl.class,
    JpaCommentRepository.class,
    JpaBookRepository.class
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CommentServiceImplTest {

  @Autowired
  private CommentService commentService;

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenFindCommentById() {
    Optional<CommentDto> commentDto = commentService.findCommentById(1L);

    assertDoesNotThrow(() -> {
      commentDto.get().bookId();
      commentDto.get().comment();
    });
    assertThat(commentDto).isPresent();
    assertThat(commentDto.get().bookId()).isNotNull();
  }

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenFindAllById() {
    List<CommentDto> comments = commentService.findAllById(1L);

    assertDoesNotThrow(() -> {
      comments.get(0).bookId();
      comments.get(0).comment();
    });
    assertThat(comments).isNotEmpty();
  }

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenAddComment() {
    Optional<CommentDto> commentDto = commentService.addComment(1L, "New comment");

    assertDoesNotThrow(() -> {
      commentDto.get().bookId();
      commentDto.get().comment();
    });
    assertThat(commentDto).isPresent();
    assertThat(commentDto.get().bookId()).isEqualTo(1L);
  }
}
