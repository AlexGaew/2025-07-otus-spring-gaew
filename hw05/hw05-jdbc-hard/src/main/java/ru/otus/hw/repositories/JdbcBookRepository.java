package ru.otus.hw.repositories;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

  private final GenreRepository genreRepository;

  private final NamedParameterJdbcTemplate jdbc;

  @Override
  public Optional<Book> findById(long id) {
    Book book = jdbc.query(
        "select b.id, b.title, b.author_id, a.full_name, g.id as genre_id, g.name as genre_name "
            + "from books b "
            + "left join authors a ON b.author_id = a.id "
            + "left join books_genres bg ON b.id = bg.book_id "
            + "left join genres g ON bg.genre_id = g.id "
            + "where b.id = :id", Map.of("id", id),
        new BookResultSetExtractor());

    return Optional.ofNullable(book);
  }

  @Override
  public List<Book> findAll() {
    var genres = genreRepository.findAll();
    var books = getAllBooksWithoutGenres();
    var relations = getAllGenreRelations();
    mergeBooksInfo(books, genres, relations);
    return books;
  }

  @Override
  public Book save(Book book) {
    if (book.getId() == 0) {
      return insert(book);
    }
    return update(book);
  }

  @Override
  public void deleteById(long id) {
    jdbc.update("delete from books where id = :id", Map.of("id", id));
  }

  private List<Book> getAllBooksWithoutGenres() {
    return jdbc.query("select b.id as id, b.title as title, b.author_id as author_id, a.full_name as full_name "
            + "from  books b left join authors a on b.author_id = a.id",
        new BookRowMapper());
  }

  private List<BookGenreRelation> getAllGenreRelations() {
    return jdbc.query("select book_id, genre_id from books_genres", new BookGenresRowMapper());
  }

  private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
      List<BookGenreRelation> relations) {

    Map<Long, Book> bookMap = booksWithoutGenres.stream()
        .collect(Collectors.toMap(Book::getId, book -> book));

    Map<Long, Genre> genreMap = genres.stream()
        .collect(Collectors.toMap(Genre::getId, genre -> genre));

    for (BookGenreRelation relation : relations) {
      Book book = bookMap.get(relation.bookId);
      Genre genre = genreMap.get(relation.genreId);

      if (book != null && genre != null) {
        book.getGenres().add(genre);
      }
    }
  }

  private Book insert(Book book) {
    var keyHolder = new GeneratedKeyHolder();

    jdbc.update("insert into books (title, author_id) values (:title, :authorId)",
        new MapSqlParameterSource()
            .addValue("title", book.getTitle())
            .addValue("authorId", book.getAuthor().getId())
        , keyHolder);

    //noinspection DataFlowIssue
    book.setId(keyHolder.getKeyAs(Long.class));
    batchInsertGenresRelationsFor(book);
    return book;
  }

  private Book update(Book book) {

    removeGenresRelationsFor(book);

    batchInsertGenresRelationsFor(book);

    int rowsAffected = jdbc.update("UPDATE books SET title = :title, author_id = :authorId WHERE id = :id",
        Map.of("title", book.getTitle(), "authorId", book.getAuthor().getId(), "id", book.getId()));

    if (rowsAffected == 0) {
      throw new EntityNotFoundException("Book with id " + book.getId() + " not found");
    }

    return book;
  }

  private void batchInsertGenresRelationsFor(Book book) {
    if (book.getGenres() == null || book.getGenres().isEmpty()) {
      return;
    }
    List<Genre> genres = book.getGenres();

    jdbc.batchUpdate(
        "insert into books_genres (book_id, genre_id) VALUES (:bookId, :genreId)",
        SqlParameterSourceUtils.createBatch(
            book.getGenres().stream()
                .map(g -> Map.of("bookId", book.getId(), "genreId", g.getId()))
                .toArray()));
  }

  private void removeGenresRelationsFor(Book book) {

    jdbc.update("delete from books_genres where book_id = :bookId", Map.of("bookId", book.getId()));
  }

  @SuppressWarnings("ClassCanBeRecord")
  @RequiredArgsConstructor
  private static class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {

      var id = rs.getLong("id");
      var title = rs.getString("title");
      var authorId = rs.getLong("author_id");
      var authorName = rs.getString("full_name");

      Author author = new Author(authorId, authorName);
      return new Book(id, title, author, new ArrayList<>());
    }
  }

  private static class BookGenresRowMapper implements RowMapper<BookGenreRelation> {

    @Override
    public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
      var id = rs.getLong("book_id");
      var genreId = rs.getLong("genre_id");
      return new BookGenreRelation(id, genreId);
    }
  }

  @SuppressWarnings("ClassCanBeRecord")
  @RequiredArgsConstructor
  private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

    @Override
    public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
      if (!rs.next()) {
        return null;
      }
      var id = rs.getLong("id");
      var title = rs.getString("title");
      var authorId = rs.getLong("author_id");
      var authorName = rs.getString("full_name");

      Author author = new Author(authorId, authorName);
      List<Genre> genres = new ArrayList<>();

      do {
        long genreId = rs.getLong("genre_id");
        if (!rs.wasNull()) {
          String genreName = rs.getString("genre_name");
          genres.add(new Genre(genreId, genreName));
        }
      } while (rs.next());
      return new Book(id, title, author, genres);
    }
  }

  private record BookGenreRelation(long bookId, long genreId) {

  }
}
