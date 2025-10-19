package ru.otus.hw.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

@DisplayName("Репозиторий на основе Jpa для работы с Жанрами ")
@DataJpaTest
@Import({JpaCommentRepository.class, JpaBookRepository.class})
class JpaCommentRepositoryTest {

  @Autowired
  private CommentRepository jpaCommentRepository;

  @Autowired
  private BookRepository jpaBookRepository;

  @Autowired
  private EntityManager em;

  private List<Comment> dbComment;

  @BeforeEach
  void setUp() {
    dbComment = getDbComment();
  }

  @DisplayName("должен загружать comment по id")
  @Test
  void shouldReturnCorrectCommentById() {
    var expectedComment = dbComment.get(0);

    var actualComment = jpaCommentRepository.findById(1).get();

    assertThat(actualComment).usingRecursiveComparison()
        .ignoringFields("book")
        .isEqualTo(expectedComment);
    System.out.println(actualComment);
  }

  @DisplayName("должен загружать список всех comments по book id")
  @Test
  void shouldReturnCorrectCommentList() {
    var book = jpaBookRepository.findById(1L).get();
    var expectedComments = jpaCommentRepository.findByBookId(book.getId());
    var actualComments = book.getComments();

    assertThat(actualComments).usingRecursiveComparison()
        .isEqualTo(expectedComments);
    actualComments.forEach(System.out::println);
  }

  @DisplayName("должен добавить комментарий к книге")
  @Test
  void shouldAddCommentToBook() {
    var book = jpaBookRepository.findById(1L).get();
    var newComment = new Comment(0, "New comment", book);
    var returnComment = jpaCommentRepository.save(newComment);
    em.flush();
    em.clear();
    var actualComment = jpaBookRepository.findById(1L).get().getComments().get(2);

    assertThat(actualComment)
        .usingRecursiveComparison()
        .ignoringFields("book")
        .isEqualTo(returnComment);
  }

  private static List<Comment> getDbComment() {
    return IntStream.range(1, 7).boxed()
        .map(id -> new Comment(id, "Comment_" + id, null))
        .toList();
  }
}