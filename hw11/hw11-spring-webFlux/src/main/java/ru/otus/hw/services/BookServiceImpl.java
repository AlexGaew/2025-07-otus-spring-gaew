package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

  private final AuthorRepository authorRepository;

  private final GenreRepository genreRepository;

  private final BookRepository bookRepository;

  private final CommentRepository commentRepository;

  @Override
  public Mono<BookDto> findById(String id) {
    var book = bookRepository.findById(id);
    return book.map(BookDto::from);
  }

  @Override
  public Flux<BookDto> findAll() {
    var books = bookRepository.findAll();
    return books.map(BookDto::from);
  }

  @Override
  public Mono<BookDto> insert(String title, String authorId, List<String> genresIds) {
    return save(null, title, authorId, genresIds);
  }

  @Override
  public Mono<BookDto> update(String id, String title, String authorId, List<String> genresIds) {
    return save(id, title, authorId, genresIds);
  }

  @Override
  public Mono<Void> deleteById(String bookId) {
    return commentRepository.deleteByBookId(bookId)
        .then(bookRepository.deleteById(bookId));
  }

  private Mono<BookDto> save(String id, String title, String authorId, List<String> genresIds) {
    if (isEmpty(genresIds)) {
      throw new IllegalArgumentException("Genres ids must not be null");
    }

    var authorMono = authorRepository.findById(authorId)
        .switchIfEmpty(Mono.error(new EntityNotFoundException("Author not found")));

    var genresMono = Flux.fromIterable(genresIds)
        .flatMap(genreRepository::findById)
        .collectList()
        .flatMap(genres -> {
          if (isEmpty(genres) || genres.size() != genresIds.size()) {
            return Mono.error(new EntityNotFoundException("Genre not found"));
          }
          return Mono.just(genres);
        });

    return Mono.zip(authorMono, genresMono)
        .flatMap(tuple -> {
          var book = new Book(id, title, tuple.getT1(), tuple.getT2());
          return bookRepository.save(book);
        })
        .map(BookDto::from);
  }
}
