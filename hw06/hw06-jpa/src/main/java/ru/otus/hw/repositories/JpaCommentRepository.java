package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

  private final EntityManager em;

  @Override
  public Optional<Comment> findById(long id) {
    TypedQuery<Comment> query = em.createQuery("select c from Comment c where c.id = :id", Comment.class)
        .setParameter("id", id);
    try {
      return Optional.of(query.getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<Comment> findByBookId(Long bookId) {
    TypedQuery<Comment> query = em.createQuery("select c from Comment c where c.book.id = :bookId ", Comment.class)
        .setParameter("bookId", bookId);
    return query.getResultList();
  }

  @Override
  public Comment save(Comment comment) {
    if (comment.getId() == 0) {
      em.persist(comment);
      return comment;
    }
    return em.merge(comment);
  }
}
