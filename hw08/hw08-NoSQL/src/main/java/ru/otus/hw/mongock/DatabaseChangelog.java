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
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@ChangeLog
public class DatabaseChangelog {

  @ChangeSet(order = "001", id = "dropDb", author = "GtwoA", runAlways = true)
  public void dropDb(MongoDatabase db) {
    db.drop();
  }
//
//  @ChangeSet(order = "002", id = "insertAuthors", author = "GtwoA")
//  public void insertAuthors(AuthorRepository repository) {
//    List<Author> authors = new ArrayList<>();
//    authors.add(new Author("Pushkin"));
//    authors.add(new Author("Dostoevsky"));
//    authors.add(new Author("Gogol"));
//    repository.saveAll(authors);
//  }

//  @ChangeSet(order = "003", id = "insertGenres", author = "GtwoA")
//  public void insertGenres(GenreRepository repository) {
//    List<Genre> genres = new ArrayList<>();
//    genres.add(new Genre("Roman"));
//    genres.add(new Genre("Anime"));
//    genres.add(new Genre("Detective"));
//    repository.saveAll(genres);
//  }

  @ChangeSet(order = "002", id = "insertBooks", author = "GtwoA")
  public void insertBooks(BookRepository repository) {
    List<Book> books = new ArrayList<>();
    books.add(new Book("1", "title_1", new Author("Pushkin"), List.of(new Genre("Roman"))));
    books.add(new Book("2","title_2", new Author("Gogol"),
        List.of(new Genre("Roman"), new Genre("Anime"))));
    books.add(new Book("3","title_3", new Author("Dostoevsky"), List.of(new Genre("Detective"))));
    repository.saveAll(books);
  }

  @ChangeSet(order = "003", id = "insertBooksComments", author = "GtwoA")
  public void insertComments(CommentRepository repository) {
    List<Comment> comments = new ArrayList<>();
    comments.add(new Comment("1", "Good book_1", "1"));
    comments.add(new Comment("2", "Good book_2", "1"));
    comments.add(new Comment("3", "Bed book_1", "2"));
    comments.add(new Comment("4", "Bed book_12", "3"));
    comments.add(new Comment("5", "Good book_12", "3"));
    repository.saveAll(comments);
  }



}
