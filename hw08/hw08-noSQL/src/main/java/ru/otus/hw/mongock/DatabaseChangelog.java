package ru.otus.hw.mongock;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

@ChangeLog
public class DatabaseChangelog {

  @ChangeSet(order = "001", id = "dropDb", author = "GtwoA", runAlways = true)
  public void dropDb(MongoDatabase db) {
    db.drop();
  }

  @ChangeSet(order = "002", id = "insertAuthors", author = "GtwoA")
  public void insertAuthors(AuthorRepository repository) {
    List<Author> authors = new ArrayList<>();
    authors.add(new Author("1", "Pushkin"));
    authors.add(new Author("2", "Dostoevsky"));
    authors.add(new Author("3", "Gogol"));
    repository.saveAll(authors);
  }

  @ChangeSet(order = "003", id = "insertGenres", author = "GtwoA")
  public void insertGenres(GenreRepository repository) {
    List<Genre> genres = new ArrayList<>();
    genres.add(new Genre("1", "Roman"));
    genres.add(new Genre("2", "Anime"));
    genres.add(new Genre("3", "Detective"));
    repository.saveAll(genres);
  }

  @ChangeSet(order = "004", id = "insertBooks", author = "GtwoA")
  public void insertBooks(BookRepository repository, AuthorRepository authorRepository,
      GenreRepository genreRepository) {
    var pushkin = authorRepository.findById("1").orElseThrow();
    var gogol = authorRepository.findById("3").orElseThrow();
    var dostoevsky = authorRepository.findById("2").orElseThrow();

    var roman = genreRepository.findById("1").orElseThrow();
    var anime = genreRepository.findById("2").orElseThrow();
    var detective = genreRepository.findById("3").orElseThrow();

    List<Book> books = new ArrayList<>();
    books.add(new Book("1", "title_1", pushkin, List.of(roman)));
    books.add(new Book("2", "title_2", gogol, List.of(roman, anime)));
    books.add(new Book("3", "title_3", dostoevsky, List.of(detective)));
    repository.saveAll(books);
  }

  @ChangeSet(order = "005", id = "insertBooksComments", author = "GtwoA")
  public void insertComments(CommentRepository repository, BookRepository bookRepository) {
    var book1 = bookRepository.findById("1").orElseThrow();
    var book2 = bookRepository.findById("2").orElseThrow();
    var book3 = bookRepository.findById("3").orElseThrow();

    List<Comment> comments = new ArrayList<>();
    comments.add(new Comment("1", "Good book_1", book1));
    comments.add(new Comment("2", "Good book_2", book1));
    comments.add(new Comment("3", "Bed book_1", book2));
    comments.add(new Comment("4", "Bed book_12", book3));
    comments.add(new Comment("5", "Good book_12", book3));
    repository.saveAll(comments);
  }
}
