package ru.otus.hw.models;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@NamedEntityGraph(
    name = "user-authority-entity-graph",
    attributeNodes = {
        @NamedAttributeNode("authorities")
    }
)
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String username;

  private String password;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "user_authorities",
      joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "authority")
  private List<String> authorities;
}
