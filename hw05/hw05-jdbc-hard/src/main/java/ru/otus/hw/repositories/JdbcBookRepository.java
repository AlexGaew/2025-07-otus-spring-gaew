package ru.otus.hw.repositories;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.util.Collections.emptyList;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
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

  private final AuthorRepository authorRepository;

  private final JdbcTemplate jdbc;

  @Override
  public Optional<Book> findById(long id) {
    try {
      Book book = jdbc.queryForObject("select ID, TITLE, AUTHOR_ID from books where ID = ?",
          (rs, rowNum) -> new BookResultSetExtractor(authorRepository, genreRepository)
              .extractData(rs), id);
      if (book != null) {
        List<Book> singletonList = List.of(book);
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        mergeBooksInfo(singletonList, genres, relations);
      }

      return Optional.ofNullable(book);
    } catch (DataAccessException e) {
      return Optional.empty();
    }
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
    jdbc.update("delete from books where id = ?", id);
  }

  private List<Book> getAllBooksWithoutGenres() {
    return jdbc.query("select ID, TITLE, AUTHOR_ID from books", new BookRowMapper(authorRepository));
  }

  private List<BookGenreRelation> getAllGenreRelations() {
    return jdbc.query("select BOOK_ID, GENRE_ID from books_genres", new BookGenresRowMapper());
  }

  private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
      List<BookGenreRelation> relations) {
    Map<Long, List<Long>> relBookIdGenresMap = relations.stream()
        .collect(Collectors.groupingBy(b -> b.bookId, Collectors.mapping(b -> b.genreId, Collectors.toList())));
    Map<Long, Genre> genreMap = genres.stream()
        .collect(Collectors.toMap(Genre::getId, g -> g));

    for (Book book : booksWithoutGenres) {
      var genreIdList = relBookIdGenresMap.get(book.getId());

      if (genreIdList != null) {
        List<Genre> bookGenres = genreIdList.stream()
            .map(genreMap::get)
            .filter(Objects::nonNull)
            .toList();
        book.setGenres(bookGenres);
      } else {
        book.setGenres(emptyList());
      }
    }
  }

  private Book insert(Book book) {
    var keyHolder = new GeneratedKeyHolder();

    jdbc.update(connection -> {
      var ps = connection.prepareStatement("insert into books (title, author_id) values (?, ?)",
          RETURN_GENERATED_KEYS);
      ps.setString(1, book.getTitle());
      ps.setLong(2, book.getAuthor().getId());
      return ps;
    }, keyHolder);

    //noinspection DataFlowIssue
    book.setId(keyHolder.getKeyAs(Long.class));
    batchInsertGenresRelationsFor(book);
    return book;
  }

  private Book update(Book book) {
    Book findBook = this.findById(book.getId()).orElseThrow(() -> new EntityNotFoundException("Book not found"));

    Set<Long> genresIds = book.getGenres().stream()
        .map(Genre::getId)
        .collect(Collectors.toSet());

    List<Genre> genres = genreRepository.findAllByIds(genresIds);

    removeGenresRelationsFor(findBook);

    findBook.setGenres(genres);

    batchInsertGenresRelationsFor(book);

    jdbc.update("UPDATE books SET title = ?, author_id = ? WHERE id = ?",
        book.getTitle(), book.getAuthor().getId(), book.getId());

    return book;
  }

  private void batchInsertGenresRelationsFor(Book book) {
    if (book.getGenres() == null || book.getGenres().isEmpty()) {
      return;
    }
    List<Genre> genres = book.getGenres();

    jdbc.batchUpdate(
        "INSERT INTO books_genres (book_id, genre_id) VALUES (?, ?)",
        genres,
        genres.size(),
        (ps, genre) -> {
          ps.setLong(1, book.getId());
          ps.setLong(2, genre.getId());
        }
    );
  }

  private void removeGenresRelationsFor(Book book) {

    jdbc.update("delete from books_genres where book_id = ?", book.getId());
  }

  @SuppressWarnings("ClassCanBeRecord")
  @RequiredArgsConstructor
  private static class BookRowMapper implements RowMapper<Book> {

    private final AuthorRepository authorRepository;

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {

      var id = rs.getLong("ID");
      var title = rs.getString("TITLE");
      var authorId = rs.getLong("AUTHOR_ID");
      Author author = authorRepository.findById(authorId).orElse(null);
      return new Book(id, title, author, emptyList());
    }
  }

  private static class BookGenresRowMapper implements RowMapper<BookGenreRelation> {

    @Override
    public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
      var id = rs.getLong("BOOK_ID");
      var genreId = rs.getLong("GENRE_ID");
      return new BookGenreRelation(id, genreId);
    }
  }

  @SuppressWarnings("ClassCanBeRecord")
  @RequiredArgsConstructor
  private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    @Override
    public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
      long id = rs.getLong("ID");

      String title = rs.getString("TITLE");
      var authorId = rs.getLong("AUTHOR_ID");
      Author author = authorRepository.findById(authorId).orElse(null);

      return new Book(id, title, author, emptyList());
    }
  }

  private record BookGenreRelation(long bookId, long genreId) {

  }
}
