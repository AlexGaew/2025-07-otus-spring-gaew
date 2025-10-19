package ru.otus.hw.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(
    name = "book-full",
    attributeNodes = {
        @NamedAttributeNode("author"),
        @NamedAttributeNode("genres"),
        @NamedAttributeNode("comments"),
    }
)
@NamedEntityGraph(
    name = "book-list",
    attributeNodes = {
        @NamedAttributeNode("author"),
        @NamedAttributeNode("genres"),
    }
)
@Entity
@Getter
@Table(name = "books")
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "title")
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  private Author author;

  @ManyToMany(targetEntity = Genre.class, fetch = FetchType.LAZY)
  @JoinTable(
      name = "books_genres",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "genre_id")
  )
  private List<Genre> genres;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
  private List<Comment> comments;

}
