package ru.otus.hw.services;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

  private final AuthorRepository authorRepository;

  @Override
  @Transactional(readOnly = true)
  public List<AuthorDto> findAll() {
    List<Author> authors = authorRepository.findAll();

    if (authors.isEmpty()) {
      throw new EntityNotFoundException("Authors not found");
    }

    return authors.stream().map(AuthorDto::from)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<AuthorDto> findById(long id) {
    var author = authorRepository.findById(id);
    return author.map(AuthorDto::from);
  }
}
