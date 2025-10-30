package ru.otus.hw.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
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
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Comment;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями ")
@DataJpaTest
@Import({JpaCommentRepository.class, JpaBookRepository.class})
class JpaCommentRepositoryTest {

  @Autowired
  private CommentRepository jpaCommentRepository;

  @Autowired
  private BookRepository jpaBookRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private EntityManager em;

  private List<Comment> dbComments;

  @BeforeEach
  void setUp() {
    dbComments = getDbComments();
  }

  @DisplayName("должен загружать комментарий по id")
  @ParameterizedTest
  @MethodSource("getDbComments")
  void shouldReturnCorrectCommentById(Comment expectedComment) {
    var actualComment = jpaCommentRepository.findById(expectedComment.getId());

    assertThat(actualComment).isPresent()
        .get()
        .usingRecursiveComparison()
        .ignoringFields("book")
        .isEqualTo(expectedComment);
  }

  @DisplayName("должен загружать список всех комментариев по book id")
  @Test
  void shouldReturnCorrectCommentsByBookId() {
    var book = jpaBookRepository.findById(1L);
    assertThat(book).isPresent();

    var actualComments = jpaCommentRepository.findByBookId(book.get().getId());

    assertThat(actualComments).isNotEmpty()
        .hasSize(2);
  }

  @DisplayName("должен сохранять новый комментарий")
  @Test
  void shouldSaveNewComment() {
    var book = jpaBookRepository.findById(1L);
    assertThat(book).isPresent();

    var newComment = new Comment(0, "New comment", book.get());
    var returnedComment = jpaCommentRepository.save(newComment);
    em.flush();
    em.clear();

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