package ru.otus.hw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  @GetMapping("/books")
  public String listBooks() {
    return "list-books-fetch";
  }

  @GetMapping("/book/create")
  public String createBook() {
    return "create-book-fetch";
  }

  @GetMapping("/book/{id}/edit")
  public String editBook() {
    return "edit-book-fetch";
  }

  @GetMapping("/book/{id}/info")
  public String bookInfo() {
    return "book-info-fetch";
  }

  @GetMapping("/authors")
  public String listAuthors() {
    return "list-authors-fetch";
  }

  @GetMapping("/genres")
  public String listGenres() {
    return "list-genres-fetch";
  }
}