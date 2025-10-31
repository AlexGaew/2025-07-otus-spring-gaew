package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;

  private final CommentRepository commentRepository;

  @Override
  public Optional<BookDto> findById(String id) {
    var book = bookRepository.findById(id);
    return book.map(BookDto::from);
  }

  @Override
  public List<BookDto> findAll() {
    var books = bookRepository.findAll();
    return books.stream().map(BookDto::from)
        .toList();
  }

  @Override
  public BookDto insert(String title, String authorName, List<Genre> genres) {

    return save(null, title, authorName, genres);
  }

  @Override
  public BookDto update(String id, String title, String authorName, List<Genre> genres) {
    return save(id, title, authorName, genres);
  }

  @Override
  public void deleteById(String bookId) {
    var comments = commentRepository.findByBookId(bookId);
    commentRepository.deleteAll(comments);
    bookRepository.deleteById(bookId);
  }

  private BookDto save(String id, String title, String authorName, List<Genre> genres) {
    if (isEmpty(genres)) {
      throw new IllegalArgumentException("Genres must not be null");
    }
    var author = new Author(authorName);
    var book = new Book(id, title, author, genres);
    var bookSaved = bookRepository.save(book);

    return BookDto.from(bookSaved);
  }
}
