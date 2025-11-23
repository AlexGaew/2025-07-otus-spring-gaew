package ru.otus.hw.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookController {

  private static final int CREATED = 201;

  private final BookService bookService;


  @GetMapping("/book")
  public List<BookDto> getAllBooks() {
    return bookService.findAll();
  }

  @GetMapping("/book/{id}")
  public ResponseEntity<BookDto> getBookById(@PathVariable long id) {
    return ResponseEntity.ok(bookService.findById(id));
  }

  @PostMapping("/book")
  public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookRequestDto bookRequestDto) {
    var savedBook = bookService.insert(bookRequestDto.getTitle(), bookRequestDto.getAuthorId(),
        bookRequestDto.getGenreIds());
    return ResponseEntity.status(CREATED).body(savedBook);
  }

  @DeleteMapping("/book/{id}")
  public ResponseEntity<?> deleteBook(@PathVariable("id") long id) {
    bookService.deleteById(id);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/book/{id}")
  public ResponseEntity<BookDto> updateBook(@PathVariable("id") long id,
      @Valid @RequestBody BookRequestDto bookRequestDto) {
    var updatedBook = bookService.update(id, bookRequestDto.getTitle(), bookRequestDto.getAuthorId(),
        bookRequestDto.getGenreIds());
    return ResponseEntity.ok(updatedBook);
  }
}
