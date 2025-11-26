package ru.otus.hw.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями ")
@DataJpaTest
class CommentRepositoryTest {

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  private List<Comment> dbComments;

  @BeforeEach
  void setUp() {
    dbComments = getDbComments();
  }

  @DisplayName("должен загружать комментарий по id")
  @ParameterizedTest
  @MethodSource("getDbComments")
  void shouldReturnCorrectCommentById(Comment expectedComment) {
    var actualComment = commentRepository.findById(expectedComment.getId());

    assertThat(actualComment).isPresent()
        .get()
        .usingRecursiveComparison()
        .ignoringFields("book")
        .isEqualTo(expectedComment);
  }

  @DisplayName("должен загружать список всех комментариев по book id")
  @Test
  void shouldReturnCorrectCommentsByBookId() {
    var book = bookRepository.findById(1L);
    assertThat(book).isPresent();

    var actualComments = commentRepository.findByBookId(book.get().getId());

    assertThat(actualComments).isNotEmpty()
        .hasSize(2);
  }

  @DisplayName("должен сохранять новый комментарий")
  @Test
  void shouldSaveNewComment() {
    var book = bookRepository.findById(1L);
    assertThat(book).isPresent();

    var newComment = new Comment(0, "New comment", book.get());
    var returnedComment = commentRepository.save(newComment);

    Comment actualComment = testEntityManager.find(Comment.class, returnedComment.getId());
    assertThat(actualComment).isNotNull();
    assertThat(actualComment.getId()).isEqualTo(returnedComment.getId());
    assertThat(actualComment.getComment()).isEqualTo(returnedComment.getComment());
    assertThat(actualComment.getBook().getId()).isEqualTo(book.get().getId());
  }

  private static List<Comment> getDbComments() {
    return IntStream.range(1, 7).boxed()
        .map(id -> new Comment(id, "Comment_" + id, null))
        .toList();
  }
}