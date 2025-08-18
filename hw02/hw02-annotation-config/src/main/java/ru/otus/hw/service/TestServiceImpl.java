package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

  private static final String ERROR_MESSAGE = "Please, enter number answer from 1 to %s";

  private static final int INDEX_OFFSET = 1;

  private final IOService ioService;

  private final QuestionDao questionDao;

  @Override
  public TestResult executeTestFor(Student student) {
    ioService.printLine("");
    ioService.printFormattedLine("Please answer the questions below%n");
    var questions = questionDao.findAll();
    var testResult = new TestResult(student);

    for (var question : questions) {
      var isAnswerValid = false;
      ioService.printFormattedLine("Question: %s", question.text());
      ioService.printLine("Answers:");
      printAnswer(question);

      if (checkAnswer(question)) {
        isAnswerValid = true;
      }

      ioService.printLine("");
      testResult.applyAnswer(question, isAnswerValid);
    }
    return testResult;
  }

  private void printAnswer(Question question) {
    question.answers().forEach(answer -> ioService.printFormattedLine(answer.text()));
  }

  private int returnAnswerNumber(Question question) {
    var answersSize = question.answers().size();
    int number = ioService.readIntForRangeWithPrompt(0, answersSize, "Enter number answer: ",
        ERROR_MESSAGE.formatted(answersSize));

    return number - INDEX_OFFSET;
  }

  private boolean checkAnswer(Question question) {
    return question.answers().get(returnAnswerNumber(question)).isCorrect();
  }
}
