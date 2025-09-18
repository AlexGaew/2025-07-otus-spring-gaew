package ru.otus.hw.models;

import static java.util.Collections.emptyList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

  private long id;

  private String title;

  private Author author;

  private List<Genre> genres;

  public Book(long id, String title, Author author) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.genres = emptyList();
  }
}
