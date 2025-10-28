package ru.otus.hw.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@SpringBootTest
public class CommentServiceImplTest {

  @Autowired
  private CommentService commentService;

  @Test
  void shouldNotThrowLazyInitializationExceptionWhenAccessingBook() {
    Optional<CommentDto> commentDto = commentService.findCommentById(1L);

    assertThat(commentDto).isPresent();
    assertThat(commentDto.get().bookId()).isNotNull();
  }

}