package ru.otus.hw.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

  @Query("select b from Book b join fetch b.author left join fetch b.genres where b.id = :id")
  Optional<Book> findById(@Param("id") long id);

  @EntityGraph("author-entity-graph")
  List<Book> findAll();

}
