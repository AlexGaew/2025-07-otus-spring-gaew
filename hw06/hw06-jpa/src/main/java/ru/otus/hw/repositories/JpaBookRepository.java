package ru.otus.hw.repositories;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

  private final EntityManager em;

  @Override
  public Optional<Book> findById(long id) {
    EntityGraph<?> entityGraph = em.getEntityGraph("book-full");
    Map<String, Object> hints = Map.of(FETCH.getKey(), entityGraph);
    Book book = em.find(Book.class, id, hints);
    return Optional.ofNullable(book);
  }

  @Override
  public List<Book> findAll() {
    EntityGraph<?> entityGraph = em.getEntityGraph("book-list");
    TypedQuery<Book> query = em.createQuery("select b from Book b", Book.class);
    query.setHint(FETCH.getKey(), entityGraph);
    return query.getResultList();
  }

  @Override
  public Book save(Book book) {
    if (book.getId() == 0) {
      em.persist(book);
      return book;
    }
    return em.merge(book);
  }

  @Override
  public void deleteById(long id) {
    Query query = em.createQuery("delete from Book where id = :id")
        .setParameter("id", id);
    query.executeUpdate();
  }

  @Override
  public Book update(Book book) {
    return em.merge(book);
  }
}
