package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

  private final AuthorRepository authorRepository;

  private final GenreRepository genreRepository;

  private final BookRepository bookRepository;

  @Override
  @Transactional(readOnly = true)
  public Optional<BookDto> findById(long id) {
    var book = bookRepository.findById(id);
    return book.map(BookDto::from);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BookDto> findAll() {
    var books = bookRepository.findAll();
    return books.stream().map(BookDto::from)
        .toList();
  }

  @Override
  @Transactional
  public BookDto insert(String title, long authorId, List<Long> genresIds) {
    return save(0, title, authorId, genresIds);
  }

  @Override
  @Transactional
  public BookDto update(long id, String title, long authorId, List<Long> genresIds) {
    return save(id, title, authorId, genresIds);
  }

  @Override
  @Transactional
  public void deleteById(long id) {
    bookRepository.deleteById(id);
  }

  private BookDto save(long id, String title, long authorId, List<Long> genresIds) {
    if (isEmpty(genresIds)) {
      throw new IllegalArgumentException("Genres ids must not be null");
    }

    var author = authorRepository.findById(authorId)
        .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));

    var genres = genreRepository.findAllByIds(genresIds);

    if (isEmpty(genres) || genresIds.size() != genres.size()) {
      throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
    }

    var book = new Book(id, title, author, genres);
    var bookSaved = bookRepository.save(book);

    return BookDto.from(bookSaved);
  }
}
