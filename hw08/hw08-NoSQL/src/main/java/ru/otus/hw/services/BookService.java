package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Optional;
import ru.otus.hw.models.Genre;

public interface BookService {

  Optional<BookDto> findById(String id);

  List<BookDto> findAll();

  BookDto insert(String title, String authorName, List<Genre> genres);

  BookDto update(String id, String title,   String authorName, List<Genre> genres);

  void deleteById(String id);
}
