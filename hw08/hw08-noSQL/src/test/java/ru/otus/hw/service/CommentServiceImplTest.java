package ru.otus.hw.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CommentServiceImpl;

@DataMongoTest
@Import({CommentServiceImpl.class})
public class CommentServiceImplTest {

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private AuthorRepository authorRepository;

  @Autowired
  private GenreRepository genreRepository;

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

    authorRepository.saveAll(dbAuthors);
    genreRepository.saveAll(dbGenres);
    bookRepository.saveAll(dbBooks);
    commentRepository.saveAll(dbComments);
  }

  @DisplayName("должен найти все комментарии по id книги")
  @Test
  void shouldFindAllCommentById() {

    var actualComments = dbComments.stream()
        .map(CommentDto::from)
        .toList();

    var foundComments = commentService.findAllById(dbBooks.get(0).getId());

    assertThat(foundComments).isEqualTo(actualComments);

  }

  @DisplayName("должен найти комментарий по id")
  @Test
  void shouldFindCommentsById() {
    var actualComment = CommentDto.from(dbComments.get(0));

    var expectedComment = commentService.findCommentById(dbComments.get(0).getId()).get();

    assertThat(actualComment)
        .usingRecursiveComparison()
        .isEqualTo(expectedComment);
  }

  @DisplayName("должен добавить комментарий к книге по id книги")
  @Test
  void shouldAddCommentByBookId() {

    var savedComment = commentService.addComment(dbBooks.get(0).getId(), "Add Comment1");
    var foundComments = commentService.findCommentById(savedComment.get().id());

    assertThat(savedComment.get().comment())
        .isEqualTo(foundComments.get().comment());
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

  private static List<Comment> getDbComments() {
    return IntStream.range(1, 7).boxed()
        .map(id -> new Comment(String.valueOf(id), "Comment_" + id, getDbBooks().get(0)))
        .toList();
  }
}
