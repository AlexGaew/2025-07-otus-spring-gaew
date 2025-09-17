package ru.otus.hw.repositories;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    return Optional.ofNullable(
        jdbc.queryForObject("select ID, TITLE from books where ID = ?",
            (rs, rowNum) -> new BookResultSetExtractor(authorRepository).extractData(rs), id));
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
    return new ArrayList<>();
  }

  private List<BookGenreRelation> getAllGenreRelations() {
    return new ArrayList<>();
  }

  private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
      List<BookGenreRelation> relations) {
    // Добавить книгам (booksWithoutGenres) жанры (genres) в соответствии со связями (relations)
  }

  private Book insert(Book book) {
    var keyHolder = new GeneratedKeyHolder();
    jdbc.update("insert into books (id, title, author_id) values (?, ?, ?)",
        book.getId(), book.getTitle(), book.getAuthor().getId());
    //...

    //noinspection DataFlowIssue
    book.setId(keyHolder.getKeyAs(Long.class));
    batchInsertGenresRelationsFor(book);
    return book;
  }

  private Book update(Book book) {
    //...

    // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД
    removeGenresRelationsFor(book);
    batchInsertGenresRelationsFor(book);

    return book;
  }

  private void batchInsertGenresRelationsFor(Book book) {
    // Использовать метод batchUpdate
  }

  private void removeGenresRelationsFor(Book book) {
    //...
  }

  private static class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
      return null;
    }
  }

  @SuppressWarnings("ClassCanBeRecord")
  @RequiredArgsConstructor
  private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

    private final AuthorRepository authorRepository;

    @Override
    public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
      long id = rs.getLong("ID");
      String title = rs.getString("TITLE");
      Author author = authorRepository.findById(id).orElse(null);
      List<Genre> genres = Collections.emptyList();

      return new Book(id, title, author, genres);
    }
  }

  private record BookGenreRelation(long bookId, long genreId) {

  }
}
