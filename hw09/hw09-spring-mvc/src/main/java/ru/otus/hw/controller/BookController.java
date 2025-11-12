package ru.otus.hw.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCreateRequest;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

@Controller
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  private final AuthorService authorService;

  private final GenreService genreService;

  private final CommentService commentService;

  @GetMapping("/books")
  public String showAllBooks(Model model) {
    List<BookDto> books = bookService.findAll();
    model.addAttribute("books", books);
    return "list-books";
  }

  @GetMapping("/comments-book/{bookId}")
  public String showCommentsBook(@PathVariable("bookId") long id, Model model) {
    List<CommentDto> comments = commentService.findAllById(id);
    BookDto book = bookService.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
    model.addAttribute("comments", comments);
    model.addAttribute("book", book);
    return "book-info";
  }

  @GetMapping("/book/create")
  public String showCreateForm(Model model) {
    List<AuthorDto> authors = authorService.findAll();
    List<GenreDto> genres = genreService.findAll();
    model.addAttribute("authors", authors);
    model.addAttribute("genres", genres);
    model.addAttribute("bookRequest", new BookCreateRequest());
    return "create-book";
  }

  @GetMapping("/book/{id}/edit")
  public String showEditForm(@PathVariable("id") long id, Model model) {
    BookDto book = bookService.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
    List<AuthorDto> authors = authorService.findAll();
    List<GenreDto> genres = genreService.findAll();
    model.addAttribute("book", book);
    model.addAttribute("authors", authors);
    model.addAttribute("genres", genres);
    return "edit-book";
  }

  @PostMapping("/book/{id}/edit")
  public String updateBook(
      @PathVariable("id") long id,
      @RequestParam String title,
      @RequestParam long authorId,
      @RequestParam List<Long> genreIds) {
    bookService.update(id, title, authorId, genreIds);
    return "redirect:/books";
  }

  @PostMapping("/book/create")
  public String createBook(
      @Valid @ModelAttribute("bookRequest") BookCreateRequest bookRequest,
      BindingResult bindingResult,
      Model model) {

    if (bindingResult.hasErrors()) {
      List<AuthorDto> authors = authorService.findAll();
      List<GenreDto> genres = genreService.findAll();
      model.addAttribute("authors", authors);
      model.addAttribute("genres", genres);
      return "create-book";
    }

    bookService.insert(bookRequest.getTitle(), bookRequest.getAuthorId(), bookRequest.getGenreIds());
    return "redirect:/books";
  }

  @PostMapping("/book/{id}/delete")
  public String deleteBook(@PathVariable("id") long id) {
    bookService.deleteById(id);
    return "redirect:/books";
  }

}
