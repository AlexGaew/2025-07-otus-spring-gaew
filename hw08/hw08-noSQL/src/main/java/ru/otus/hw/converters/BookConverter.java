package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;

import java.util.stream.Collectors;
import ru.otus.hw.models.Genre;

@RequiredArgsConstructor
@Component
public class BookConverter {

  public String bookToString(BookDto book) {
    var genresString = book.genres().stream()
        .map(Genre::getName)
        .map("{%s}"::formatted)
        .collect(Collectors.joining(", "));
    return "Id: %s, title: %s, author: %s, genres: [%s]".formatted(
        book.id(),
        book.title(),
        book.author().getFullName(),
        genresString);
  }
}
