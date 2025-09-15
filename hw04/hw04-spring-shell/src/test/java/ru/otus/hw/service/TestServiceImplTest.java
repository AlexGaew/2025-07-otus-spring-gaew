package ru.otus.hw.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

@SpringBootTest(classes = TestServiceImpl.class)
class TestServiceImplTest {

  @MockitoBean
  private LocalizedIOService localizedIOService;

  @MockitoBean
  private QuestionDao questionDao;

  @Autowired
  private TestServiceImpl testService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = new Student("Ivan", "Ivanov");
  }

  @Test
  @DisplayName("Should print test header and all questions with answers")
  void shouldPrintTestHeaderAndAllQuestionsWithAnswers() {
    String text = "test";
    List<Answer> answers = List.of();
    Question questionOne = new Question(text, answers);
    Question questionTwo = new Question(text, answers);
    List<Question> questions = List.of(questionOne, questionTwo);
    when(questionDao.findAll()).thenReturn(questions);

    testService.executeTestFor(student);

    verify(localizedIOService, times(3)).printLine("");
    verify(localizedIOService, times(2)).printLineLocalized("Answer.text");
    verify(localizedIOService, times(1)).printLineLocalized("TestService.answer.the.questions");
    verify(localizedIOService, times(2)).printFormattedLineLocalized("Question.text",
        questionOne.text());
    verify(questionDao, times(1)).findAll();
    verify(localizedIOService, times(2)).printFormattedLineLocalized(anyString(), any());
  }
}
