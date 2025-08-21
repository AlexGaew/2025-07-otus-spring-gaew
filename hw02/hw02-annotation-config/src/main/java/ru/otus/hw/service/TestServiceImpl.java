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

  private static final int INDEX_OFFSET = 1;

  private final IOService ioService;

  private final QuestionDao questionDao;

  @Override
  public TestResult executeTestFor(Student student) {
    displayMessage();

    return getTestResult(student);
  }

  private void displayMessage() {
    ioService.printLine("");
    ioService.printFormattedLine("Please answer the questions below%n");
  }

  private void printAnswer(Question question) {
    question.answers().forEach(answer -> ioService.printFormattedLine(answer.text()));
  }

  private int returnAnswerNumber(Question question) {
    var answersSize = question.answers().size();
    int number = ioService.readIntForRangeWithPrompt(0, answersSize, "Enter number answer: ",
        "Please, enter number answer from 1 to %s".formatted(answersSize));

    return number - INDEX_OFFSET;
  }

  private boolean checkAnswer(Question question) {
    try {
      return question.answers().get(returnAnswerNumber(question)).isCorrect();
    } catch (RuntimeException e) {
      ioService.printLine("Questions not found");
    }

    return false;
  }

  private TestResult getTestResult(Student student) {
    var testResult = new TestResult(student);
    var questions = questionDao.findAll();

    for (var question : questions) {
      ioService.printFormattedLine("Question: %s", question.text());
      ioService.printLine("Answers:");
      printAnswer(question);
      var isAnswerValid = checkAnswer(question);
      ioService.printLine("");
      testResult.applyAnswer(question, isAnswerValid);
    }

    return testResult;
  }
}
