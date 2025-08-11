package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

  private final TestService testService;

  @Override
  public void run() {
    try {
      testService.executeTest();
    } catch (Exception e) {
      System.out.println("An error occurred while executing the test. Please try again later.");
    }

  }
}
