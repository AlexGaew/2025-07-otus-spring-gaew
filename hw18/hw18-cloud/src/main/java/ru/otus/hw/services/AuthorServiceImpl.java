package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

  private final AuthorRepository authorRepository;

  @Override
  @CircuitBreaker(
      name = "authorService",
      fallbackMethod = "findAllFallback"
  )
  @Transactional(readOnly = true)
  public List<AuthorDto> findAll() {
    List<Author> authors = authorRepository.findAll();

    if (authors.isEmpty()) {
      throw new EntityNotFoundException("Authors not found");
    }

    return authors.stream().map(AuthorDto::from)
        .toList();
  }

  public List<AuthorDto> findAllFallback(Throwable ex) {
    log.warn("AuthorService.findAll is unavailable: {}", ex.getMessage());
    throw new EntityNotFoundException("Authors not found");
  }

  @Override
  @CircuitBreaker(
      name = "authorService",
      fallbackMethod = "findAuthorByIdFallback"
  )
  @Transactional(readOnly = true)
  public Optional<AuthorDto> findById(long id) {
    var author = authorRepository.findById(id);
    return author.map(AuthorDto::from);
  }

  public Optional<AuthorDto> findAuthorByIdFallback(long id, Throwable ex) {
    log.warn("AuthorService.findById is unavailable: {}", ex.getMessage());
    return Optional.empty();
  }
}
