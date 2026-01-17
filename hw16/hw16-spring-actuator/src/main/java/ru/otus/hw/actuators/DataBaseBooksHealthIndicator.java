package ru.otus.hw.actuators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.BookService;

@Component
@RequiredArgsConstructor
public class DataBaseBooksHealthIndicator implements HealthIndicator {

  private final BookService bookService;

  @Override
  public Health health() {
    var books = bookService.findAll();
    if (books.isEmpty()) {
      return Health.down()
          .status(Status.DOWN)
          .withDetail("message", "Библиотека книг пуста!!! Обратите внимание!!!")
          .build();
    } else {
      return Health.up().withDetail("message", "Библиотека готова к работе!!!").build();
    }
  }
}
