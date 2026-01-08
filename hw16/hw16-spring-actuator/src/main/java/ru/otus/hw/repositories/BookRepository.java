package ru.otus.hw.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Book;

@RepositoryRestResource(path = "book-rest")
public interface BookRepository extends JpaRepository<Book, Long> {

  @EntityGraph("author-genres-entity-graph")
  Optional<Book> findById(@Param("id") long id);

  @EntityGraph("author-entity-graph")
  List<Book> findAll();

  @EntityGraph("author-entity-graph")
  @RestResource(path = "by-title", rel = "byTitle")
  List<Book> findByTitleContaining(@Param("title") String title);

}
