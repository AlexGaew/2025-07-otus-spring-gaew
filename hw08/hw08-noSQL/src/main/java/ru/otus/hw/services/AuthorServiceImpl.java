package ru.otus.hw.services;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

  private final AuthorRepository authorRepository;

  @Override
  public List<AuthorDto> findAll() {
    List<Author> authors = authorRepository.findAll();
    return authors.stream().map(AuthorDto::from)
        .toList();
  }

  @Override
  public Optional<AuthorDto> findById(String id) {
    var author = authorRepository.findById(id);
    return author.map(AuthorDto::from);
  }
}
