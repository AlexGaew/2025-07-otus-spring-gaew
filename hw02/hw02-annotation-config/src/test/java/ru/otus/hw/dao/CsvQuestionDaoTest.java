package ru.otus.hw.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

  @Mock
  private TestFileNameProvider fileNameProvider;

  @InjectMocks
  private CsvQuestionDao csvQuestionDao;

  @BeforeEach
  void setUp() {
    when(fileNameProvider.getTestFileName()).thenReturn("questions.csv");
  }

  @Test
  public void positiveCaseWhenReadingFile() {
    List<Question> questions = csvQuestionDao.findAll();
    List<String> text = questions.stream()
        .map(Question::text)
        .toList();

    verify(fileNameProvider, times(1)).getTestFileName();
    assertThat(questions).isNotEmpty();
    assertThat(questions).hasSize(3);
    assertThat(questions.get(0)).isInstanceOf(Question.class);
    assertThat(questions).contains(questions.get(0));
    assertThat(questions).extracting(Question::text).containsExactlyElementsOf(text);
  }

  @Test
  public void negativeCaseFileNotFound() {
    when(fileNameProvider.getTestFileName()).thenReturn("questions1.csv");

    assertThatThrownBy(() -> csvQuestionDao.findAll())
        .isInstanceOf(QuestionReadException.class)
        .hasMessage("File not found: " + fileNameProvider.getTestFileName());
  }

}
