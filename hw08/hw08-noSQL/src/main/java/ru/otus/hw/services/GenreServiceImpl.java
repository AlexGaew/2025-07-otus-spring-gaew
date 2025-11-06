package ru.otus.hw.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

  private final GenreRepository genreRepository;

  @Override
  public List<GenreDto> findAll() {
    var genres = genreRepository.findAll();
    return genres.stream()
        .map(GenreDto::from)
        .toList();
  }

  @Override
  public List<GenreDto> findAllGenresByIds(List<String> genreIds) {
    var genres = genreRepository.findAllById(genreIds);
    return GenreDto.from(genres);
  }
}
