package ru.otus.springbatch.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.springbatch.model.Notebook;

public interface NotebookRepository extends MongoRepository<Notebook, String> {}
