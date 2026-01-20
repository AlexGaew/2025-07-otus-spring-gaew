package ru.otus.hw.actuators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.BookRepository;

@Component
@RequiredArgsConstructor
public class DataBaseBooksHealthIndicator implements HealthIndicator {

  private static final long EMPTY_BOOK_LIBRARY = 0;

  private final BookRepository repository;

  @Override
  public Health health() {
    var countBooks = repository.count();
    if (countBooks <= EMPTY_BOOK_LIBRARY) {
      return Health.down()
          .status(Status.DOWN)
          .withDetail("message", "Библиотека книг пуста!!! Обратите внимание!!!")
          .build();
    } else {
      return Health.up().withDetail("message", "Библиотека готова к работе!!!").build();
    }
  }
}
