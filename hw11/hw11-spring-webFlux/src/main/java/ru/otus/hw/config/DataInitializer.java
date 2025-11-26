package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

  private final AuthorRepository authorRepository;

  private final GenreRepository genreRepository;

  private final BookRepository bookRepository;

  private final CommentRepository commentRepository;

  @Override
  public void run(ApplicationArguments args) {
    // Очистка БД
    cleanBd();

    // Авторы
    var pushkin = authorRepository.save(new Author("1", "Pushkin")).block();
    var dostoevsky = authorRepository.save(new Author("2", "Dostoevsky")).block();
    var gogol = authorRepository.save(new Author("3", "Gogol")).block();
    // Жанры
    var roman = genreRepository.save(new Genre("1", "Roman")).block();
    var anime = genreRepository.save(new Genre("2", "Anime")).block();
    var detective = genreRepository.save(new Genre("3", "Detective")).block();
    // Книги
    var book1 = bookRepository.save(new Book("1", "title_1", pushkin, List.of(roman))).block();
    var book2 = bookRepository.save(new Book("2", "title_2", gogol, List.of(roman, anime))).block();
    var book3 = bookRepository.save(new Book("3", "title_3", dostoevsky, List.of(detective))).block();
    // Комментарии
    commentRepository.save(new Comment("1", "Good book_1", "1")).block();
    commentRepository.save(new Comment("2", "Good book_2", "1")).block();
    commentRepository.save(new Comment("3", "Bad book_1", "2")).block();
    commentRepository.save(new Comment("4", "Bad book_12", "3")).block();
    commentRepository.save(new Comment("5", "Good book_12", "3")).block();
  }

  private void cleanBd() {
    authorRepository.deleteAll().block();
    genreRepository.deleteAll().block();
    bookRepository.deleteAll().block();
    commentRepository.deleteAll().block();
  }
}