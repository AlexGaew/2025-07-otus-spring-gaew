package ru.otus.hw.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

  private final GenreRepository genreRepository;

  @Override
  @Transactional(readOnly = true)
  public List<GenreDto> findAll() {
    var genres = genreRepository.findAll();
    return genres.stream()
        .map(g -> new GenreDto(g.getId(), g.getName()))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<GenreDto> findAllByIds(List<Long> ids) {
    var genres = genreRepository.findAllByIds(ids);
    return GenreDto.from(genres);
  }
}
