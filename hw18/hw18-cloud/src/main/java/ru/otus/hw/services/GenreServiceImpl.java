package ru.otus.hw.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.GenreRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

  private final GenreRepository genreRepository;

  @CircuitBreaker(
      name = "genreService",
      fallbackMethod = "findAllGenresFallback"
  )
  @Transactional(readOnly = true)
  public List<GenreDto> findAll() {
    var genres = genreRepository.findAll();

    if (genres.isEmpty()) {
      throw new EntityNotFoundException("Genres not found");
    }

    return genres.stream()
        .map(g -> new GenreDto(g.getId(), g.getName()))
        .toList();
  }

  public List<GenreDto> findAllGenresFallback(Throwable ex) {
    log.warn("CircuitBreaker fallback: findAllGenres", ex);
    throw new RuntimeException("Temporary error while loading genres");
  }

  @CircuitBreaker(
      name = "genreService",
      fallbackMethod = "findGenresByIdsFallback"
  )
  @Retry(name = "genreService")
  @Transactional(readOnly = true)
  public List<GenreDto> findAllByIds(List<Long> ids) {
    var genres = genreRepository.findAllById(ids);
    return GenreDto.from(genres);
  }

  public List<GenreDto> findGenresByIdsFallback(List<Long> ids, Throwable ex) {
    log.warn("CircuitBreaker fallback: findGenresByIds, ids={}", ids, ex);
    throw new RuntimeException("Temporary error while loading genres");
  }
}
