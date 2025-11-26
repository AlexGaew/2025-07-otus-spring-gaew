package ru.otus.hw.services;

import java.util.List;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;

public interface GenreService {

  Flux<GenreDto> findAll();

  Flux<GenreDto> findAllGenresByIds(List<String> genreIds);
}
