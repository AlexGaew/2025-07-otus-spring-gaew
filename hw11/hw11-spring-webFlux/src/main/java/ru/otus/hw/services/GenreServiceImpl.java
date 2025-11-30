package ru.otus.hw.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

  private final GenreRepository genreRepository;

  @Override
  public Flux<GenreDto> findAll() {
    var genres = genreRepository.findAll();
    return genres.map(GenreDto::from);
  }

  @Override
  public Flux<GenreDto> findAllGenresByIds(List<String> genreIds) {
    var genres = genreRepository.findAllById(genreIds);
    return genres.map(GenreDto::from);
  }
}
