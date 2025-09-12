package ru.otus.hw.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

  private static final int INDEX_OFFSET = 1;


  private final LocalizedIOService localizedIOService;

  private final QuestionDao questionDao;

  @Override
  public TestResult executeTestFor(Student student) {
    displayStartQuestionsMessage();

    return getTestResult(student);
  }

  private void displayStartQuestionsMessage() {
    localizedIOService.printLine("");
    localizedIOService.printLineLocalized("TestService.answer.the.questions");
  }

  private void printAnswers(List<Answer> answers) {

    answers.forEach(
        answer -> localizedIOService.printFormattedLineLocalized("Answers.text", answer.text()));
  }


  private int returnAnswerNumber(Question question) {
    var answersSize = question.answers().size();
    int number = localizedIOService.readIntForRangeWithPromptLocalized(1, answersSize,
        "Enter.number.answer",
        "Please.enter.number");

    return number - INDEX_OFFSET;
  }

  private boolean checkAnswer(Question question) {
    try {
      int answerNumber = returnAnswerNumber(question);
      return question.answers().get(answerNumber).isCorrect();
    } catch (RuntimeException e) {
      localizedIOService.printLineLocalized("Answers.not.found");
    }
    return false;
  }

  private TestResult getTestResult(Student student) {
    var testResult = new TestResult(student);
    var questions = questionDao.findAll();

    for (var question : questions) {
      localizedIOService.printFormattedLineLocalized("Question.text", question.text());
      localizedIOService.printLineLocalized("Answer.text");
      printAnswers(question.answers());
      var isAnswerValid = checkAnswer(question);
      localizedIOService.printLine("");
      testResult.applyAnswer(question, isAnswerValid);
    }
    return testResult;
  }
}
