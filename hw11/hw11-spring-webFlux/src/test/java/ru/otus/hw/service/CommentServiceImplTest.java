package ru.otus.hw.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CommentServiceImpl;

@DataMongoTest
@Import({CommentServiceImpl.class})
public class CommentServiceImplTest {

  @Autowired
  private ReactiveMongoTemplate mongoTemplate;

  @Autowired
  private CommentService commentService;


  private List<Author> dbAuthors;

  private List<Genre> dbGenres;

  private List<Book> dbBooks;

  private List<Comment> dbComments;

  @BeforeEach
  void setUp() {
    dbAuthors = getDbAuthors();
    dbGenres = getDbGenres();
    dbBooks = getDbBooks();
    dbComments = getDbComments();
    mongoTemplate.insertAll(dbAuthors).blockLast();
    mongoTemplate.insertAll(dbGenres).blockLast();
    mongoTemplate.insertAll(dbBooks).blockLast();
    mongoTemplate.insertAll(dbComments).blockLast();

  }
  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection(Genre.class).block();
    mongoTemplate.dropCollection(Book.class).block();
    mongoTemplate.dropCollection(Comment.class).block();
    mongoTemplate.dropCollection(Author.class).block();
  }


  @DisplayName("должен найти все комментарии по id книги")
  @Test
  void shouldFindAllCommentById() {
    var expectedComments = dbComments.stream()
        .filter(c -> c.getBookId().equals(dbBooks.get(0).getId()))
        .map(CommentDto::from)
        .toList();

    StepVerifier.create(commentService.findAllById(dbBooks.get(0).getId()))
        .expectNextSequence(expectedComments)
        .verifyComplete();
  }

  @DisplayName("должен найти комментарий по id")
  @Test
  void shouldFindCommentsById() {
    var expectedComment = CommentDto.from(dbComments.get(0));
    var actualComment = commentService.findCommentById(dbComments.get(0).getId()).block();

    assertThat(actualComment)
        .usingRecursiveComparison()
        .isEqualTo(expectedComment);
  }

  @DisplayName("должен добавить комментарий к книге по id книги")
  @Test
  void shouldAddCommentByBookId() {
    var savedComment = commentService.addComment(dbBooks.get(0).getId(), "Add Comment1").block();
    assertThat(savedComment).isNotNull();
    assertThat(savedComment.id()).isNotNull();

    var foundComment = commentService.findCommentById(savedComment.id()).block();
    assertThat(foundComment).isNotNull();
    assertThat(foundComment.comment()).isEqualTo("Add Comment1");
  }

  private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
    return IntStream.range(1, 4).boxed()
        .map(id -> new Book(
            String.valueOf(id),
            "BookTitle_" + id,
            dbAuthors.get(id - 1),
            dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
        )).toList();
  }

  private static List<Book> getDbBooks() {
    var dbAuthors = getDbAuthors();
    var dbGenres = getDbGenres();
    return getDbBooks(dbAuthors, dbGenres);
  }

  private static List<Author> getDbAuthors() {
    return IntStream.range(1, 4).boxed()
        .map(id -> new Author(String.valueOf(id), "Author_" + id))
        .toList();
  }

  private static List<Genre> getDbGenres() {
    return IntStream.range(1, 7).boxed()
        .map(id -> new Genre(String.valueOf(id), "Genre_" + id))
        .toList();
  }

  private List<Comment> getDbComments() {
    return IntStream.range(1, 4).boxed()
        .map(id -> new Comment(String.valueOf(id), "Comment_" + id, dbBooks.get(id - 1).getId()))
        .toList();
  }
}
