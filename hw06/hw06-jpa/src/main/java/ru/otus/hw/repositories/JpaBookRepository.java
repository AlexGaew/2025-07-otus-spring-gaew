package ru.otus.hw.repositories;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
    TypedQuery<Book> query = em.createQuery(
            "select b from Book b join fetch b.author left join fetch b.genres where b.id = :id", Book.class)
        .setParameter("id", id);
    List<Book> books = query.getResultList();

    return books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));
  }

  @Override
  public List<Book> findAll() {
    EntityGraph<?> entityGraph = em.getEntityGraph("author-entity-graph");
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
    Book book = em.getReference(Book.class, id);
    em.remove(book);
  }

  @Override
  public Book update(Book book) {
    return this.save(book);
  }
}
