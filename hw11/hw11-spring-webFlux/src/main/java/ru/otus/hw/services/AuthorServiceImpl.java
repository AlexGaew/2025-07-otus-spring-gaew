package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;


@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

  private final AuthorRepository authorRepository;

  @Override
  public Flux<AuthorDto> findAll() {
    var authors = authorRepository.findAll();
    return authors.map(AuthorDto::from);
  }

  @Override
  public Mono<AuthorDto> findById(String id) {
    var author = authorRepository.findById(id);
    return author.map(AuthorDto::from);
  }
}
