package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

  private final JdbcOperations jdbc;

  @Override
  public List<Author> findAll() {
    return jdbc.query("select ID, FULL_NAME from authors", new AuthorRowMapper());
  }

  @Override
  public Optional<Author> findById(long id) {
    return Optional.ofNullable(
        jdbc.queryForObject("select ID, FULL_NAME from authors where ID = ?",
            (rs, rowNum) -> new AuthorRowMapper().mapRow(rs, rowNum), id));
  }

  private static class AuthorRowMapper implements RowMapper<Author> {

    @Override
    public Author mapRow(ResultSet rs, int i) throws SQLException {
      long id = rs.getLong("ID");
      String fullName = rs.getString("FULL_NAME");
      return new Author(id, fullName);
    }
  }
}
