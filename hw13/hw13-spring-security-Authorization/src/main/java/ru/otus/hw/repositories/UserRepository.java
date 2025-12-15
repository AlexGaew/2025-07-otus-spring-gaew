package ru.otus.hw.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

  @EntityGraph("user-authority-entity-graph")
  Optional<User> findByUsername(String userName);

}
