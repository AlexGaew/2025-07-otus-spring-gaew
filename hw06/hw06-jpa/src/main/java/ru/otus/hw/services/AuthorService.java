package ru.otus.hw.services;

import java.util.Optional;
import ru.otus.hw.dto.AuthorDto;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();

    Optional<AuthorDto> findById(long id);
}
