package com.cortex.backend.engine.internal.parser;

import com.cortex.backend.engine.api.dto.TestCaseResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaTestResultParser implements TestResultParser {
  @Override
  public List<TestCaseResult> parseTestResults(String output) {
    List<TestCaseResult> results = new ArrayList<>();
    Pattern pattern = Pattern.compile("Tests run: (\\d+), Failures: (\\d+), Errors: (\\d+), Skipped: (\\d+)");
    Matcher matcher = pattern.matcher(output);

    if (matcher.find()) {
      int testsRun = Integer.parseInt(matcher.group(1));
      int failures = Integer.parseInt(matcher.group(2));
      int errors = Integer.parseInt(matcher.group(3));
      int skipped = Integer.parseInt(matcher.group(4));

      boolean passed = (failures == 0 && errors == 0);
      String message = passed ?
          String.format("All %d tests passed", testsRun) :
          String.format("Tests run: %d, Failures: %d, Errors: %d, Skipped: %d", testsRun, failures, errors, skipped);

      results.add(TestCaseResult.builder()
          .passed(passed)
          .input("Java Tests")
          .message(message)
          .build());
    }

    return results;
  }
}