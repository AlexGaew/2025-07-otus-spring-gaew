package ru.otus.hw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookController {


  private final BookService bookService;

  @GetMapping("/book")
  public Flux<BookDto> getAllBooks() {
    return bookService.findAll();
  }

  @GetMapping("/book/{id}")
  public Mono<ResponseEntity<BookDto>> getBookById(@PathVariable String id) {
    var book = bookService.findById(id);
    return book
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PostMapping("/book")
  public Mono<ResponseEntity<BookDto>> createBook(@Valid @RequestBody BookRequestDto bookRequestDto) {
    return bookService.insert(bookRequestDto.getTitle(), bookRequestDto.getAuthorId(), bookRequestDto.getGenreIds())
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }


  @DeleteMapping("/book/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteBook(@PathVariable("id") String id) {
    return bookService.deleteById(id);
  }

  @PatchMapping("/book/{id}")
  public Mono<ResponseEntity<BookDto>> updateBook(@PathVariable("id") String id,
      @Valid @RequestBody BookRequestDto bookRequestDto) {
    return bookService.update(id, bookRequestDto.getTitle(), bookRequestDto.getAuthorId(),
            bookRequestDto.getGenreIds())
        .map(e -> ResponseEntity.status(201).build());
  }
}
