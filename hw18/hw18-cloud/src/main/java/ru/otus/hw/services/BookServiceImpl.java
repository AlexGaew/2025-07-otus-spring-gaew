package ru.otus.hw.services;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

  private final AuthorRepository authorRepository;

  private final GenreRepository genreRepository;

  private final BookRepository bookRepository;

  private final CommentRepository commentRepository;

  @Override
  @CircuitBreaker(
      name = "bookService",
      fallbackMethod = "findBookByIdFallback"
  )
  @Retry(name = "bookService")
  @Transactional(readOnly = true)
  public BookDto findById(long id) {
    var book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
    return BookDto.from(book);
  }

  public BookDto findBookByIdFallback(long id, Throwable ex) {
    log.warn("CircuitBreaker fallback: findBookById, id={}", id, ex);
    throw new RuntimeException("Temporary error while loading book");
  }

  @Override
  @CircuitBreaker(
      name = "bookService",
      fallbackMethod = "findAllBooksFallback"
  )
  @Transactional(readOnly = true)
  public List<BookDto> findAll() {
    var books = bookRepository.findAll();
    return books.stream().map(BookDto::from)
        .toList();
  }

  public List<BookDto> findAllBooksFallback(Throwable ex) {
    log.warn("CircuitBreaker fallback: findAllBooks", ex);
    throw new RuntimeException("Temporary error while loading books");
  }

  @Override
  @CircuitBreaker(
      name = "bookService",
      fallbackMethod = "insertBookFallback"
  )
  @Retry(name = "bookService")
  @Transactional
  public BookDto insert(String title, long authorId, List<Long> genresIds) {
    return save(0, title, authorId, genresIds);
  }

  public BookDto insertBookFallback(String title, long authorId, List<Long> genresIds, Throwable ex) {
    log.warn("CircuitBreaker fallback: insertBook title='{}', authorId={}, genresIds={}", title, authorId, genresIds, ex);
    throw new RuntimeException("Temporary error while creating book");
  }

  @Override
  @CircuitBreaker(
      name = "bookService",
      fallbackMethod = "updateBookFallback"
  )
  @Retry(name = "bookService")
  @Transactional
  public BookDto update(long id, String title, long authorId, List<Long> genresIds) {
    return save(id, title, authorId, genresIds);
  }

  public BookDto updateBookFallback(long id, String title, long authorId, List<Long> genresIds, Throwable ex) {
    log.warn("CircuitBreaker fallback: updateBook id={}, title='{}', authorId={}, genresIds={}", id, title, authorId, genresIds, ex);
    throw new RuntimeException("Temporary error while updating book");
  }

  @Override
  @CircuitBreaker(
      name = "bookService",
      fallbackMethod = "deleteBookFallback"
  )
  @Transactional
  public void deleteById(long bookId) {
    var comments = commentRepository.findByBookId(bookId);
    commentRepository.deleteAll(comments);
    bookRepository.deleteById(bookId);
  }

  public void deleteBookFallback(long bookId, Throwable ex) {
    log.warn("CircuitBreaker fallback: deleteBook id={}", bookId, ex);
    throw new RuntimeException("Temporary error while deleting book");
  }

  private BookDto save(long id, String title, long authorId, List<Long> genresIds) {
    if (isEmpty(genresIds)) {
      throw new IllegalArgumentException("Genres ids must not be null");
    }

    var author = authorRepository.findById(authorId)
        .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));

    var genres = genreRepository.findAllById(genresIds);

    if (isEmpty(genres) || genresIds.size() != genres.size()) {
      throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
    }

    var book = new Book(id, title, author, genres);
    var bookSaved = bookRepository.save(book);

    return BookDto.from(bookSaved);
  }
}
