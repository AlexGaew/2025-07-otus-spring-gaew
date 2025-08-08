package ru.otus.hw.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

  @Mock
  private IOService ioService;

  @Mock
  private QuestionDao questionDao;

  @InjectMocks
  private TestServiceImpl testService;

  @Test
  @DisplayName("Should print test header and all questions with answers")
  void shouldPrintTestHeaderAndAllQuestionsWithAnswers() {
    String text = "test";
    List<Answer> answers = List.of();
    Question questionOne = new Question(text, answers);
    Question questionTwo = new Question(text, answers);
    when(questionDao.findAll()).thenReturn(List.of(questionOne, questionTwo));

    testService.executeTest();

    verify(ioService, times(1)).printLine("");
    verify(ioService, times(1)).printFormattedLine("Please answer the questions below%n");
    verify(questionDao, times(1)).findAll();
    verify(ioService, times(2)).printFormattedLine(anyString(), any());
  }
}
