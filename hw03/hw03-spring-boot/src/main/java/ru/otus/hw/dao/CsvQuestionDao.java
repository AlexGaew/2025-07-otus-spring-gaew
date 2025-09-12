package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;

import java.util.List;
import ru.otus.hw.exceptions.QuestionReadException;

@RequiredArgsConstructor
@Component
public class CsvQuestionDao implements QuestionDao {

  private static final int SKIP_LINES_COUNT = 1;

  private final TestFileNameProvider fileNameProvider;

  @Override
  public List<Question> findAll() {

    var inputStream = getClass().getClassLoader()
        .getResourceAsStream(fileNameProvider.getTestFileName());

    if (inputStream == null) {
      throw new QuestionReadException("File not found: " + fileNameProvider.getTestFileName());
    }

    try (inputStream;
        InputStreamReader reader = new InputStreamReader(inputStream)) {
      var csvToBean = new CsvToBeanBuilder<QuestionDto>(reader)
          .withSkipLines(SKIP_LINES_COUNT)
          .withSeparator(';')
          .withType(QuestionDto.class)
          .build();

      return csvToBean.parse()
          .stream()
          .map(QuestionDto::toDomainObject)
          .toList();
    } catch (IOException e) {
      throw new QuestionReadException("Could not read file", e);
    }
  }
}
