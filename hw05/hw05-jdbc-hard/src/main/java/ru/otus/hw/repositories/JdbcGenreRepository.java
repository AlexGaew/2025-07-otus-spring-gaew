package ru.otus.hw.repositories;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {

  private final JdbcTemplate jdbc;

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public List<Genre> findAll() {
    return jdbc.query("select ID, NAME from genres", new GnreRowMapper());
  }

  @Override
  public List<Genre> findAllByIds(Set<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return emptyList();
    }

    return namedParameterJdbcTemplate.query("select ID, NAME from genres where ID in (:ids)",
        singletonMap("ids", ids),
        (rs, rowNum) -> new Genre(rs.getLong("ID"),
            rs.getString("NAME")
        ));
  }

  private static class GnreRowMapper implements RowMapper<Genre> {

    @Override
    public Genre mapRow(ResultSet rs, int i) throws SQLException {
      long id = rs.getLong("ID");
      String fullName = rs.getString("NAME");
      return new Genre(id, fullName);
    }
  }
}
