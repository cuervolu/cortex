package com.cortex.backend.engine.internal.parser;

import com.cortex.backend.engine.api.dto.TestCaseResult;
import java.util.List;

public class GoTestResultParser implements TestResultParser {
  @Override
  public List<TestCaseResult> parseTestResults(String output) {
    boolean passed = output.contains("PASS");
    String testName = output.split("\t")[0].trim();
    return List.of(TestCaseResult.builder()
        .passed(passed)
        .input(testName)
        .message(passed ? "Test passed" : "Test failed")
        .build());
  }
}