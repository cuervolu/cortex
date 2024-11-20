package com.cortex.backend.engine.internal.parser;

import com.cortex.backend.engine.api.dto.TestCaseResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RustTestResultParser implements TestResultParser {
  private static final Pattern TEST_PATTERN = Pattern.compile("test ([^.]+) \\.{3} (ok|FAILED)");
  private static final Pattern SUMMARY_PATTERN = Pattern.compile(
      "test result: (ok|FAILED)\\. (\\d+) passed; (\\d+) failed; (\\d+) ignored; (\\d+) measured;.*?finished in ([\\d.]+)s");
  private static final Pattern ERROR_PATTERN = Pattern.compile("error[E\\d+]*: .*", Pattern.MULTILINE);

  @Override
  public List<TestCaseResult> parseTestResults(String output) {
    if (!isValidOutput(output)) {
      return List.of(createErrorResult("No test output provided"));
    }

    // Check for actual errors
    Matcher errorMatcher = ERROR_PATTERN.matcher(output);
    if (errorMatcher.find()) {
      return List.of(createErrorResult(errorMatcher.group()));
    }

    // Parse individual test results
    List<TestCaseResult> results = parseIndividualTests(output);

    // Add summary only if we found actual tests
    if (!results.isEmpty()) {
      TestCaseResult summaryResult = parseSummary(output);
      if (summaryResult != null) {
        results.add(summaryResult);
      }
    }

    return results;
  }

  private boolean isValidOutput(String output) {
    return output != null && !output.trim().isEmpty();
  }

  private List<TestCaseResult> parseIndividualTests(String output) {
    List<TestCaseResult> results = new ArrayList<>();
    Matcher matcher = TEST_PATTERN.matcher(output);

    while (matcher.find()) {
      String testName = matcher.group(1);
      boolean passed = "ok".equals(matcher.group(2));

      results.add(TestCaseResult.builder()
          .passed(passed)
          .input(testName)
          .expectedOutput("Test should pass")
          .actualOutput(passed ? "Test passed" : "Test failed")
          .message(passed ?
              String.format("Test '%s' passed successfully", testName) :
              String.format("Test '%s' failed", testName))
          .build());
    }

    return results;
  }

  private TestCaseResult parseSummary(String output) {
    int totalPassed = 0;
    int totalFailed = 0;
    int totalIgnored = 0;
    double totalTime = 0.0;

    Matcher matcher = SUMMARY_PATTERN.matcher(output);
    while (matcher.find()) {
      totalPassed += Integer.parseInt(matcher.group(2));
      totalFailed += Integer.parseInt(matcher.group(3));
      totalIgnored += Integer.parseInt(matcher.group(4));
      totalTime += Double.parseDouble(matcher.group(6));
    }

    if (totalPassed > 0 || totalFailed > 0) {
      boolean allPassed = totalFailed == 0;
      return TestCaseResult.builder()
          .passed(allPassed)
          .input("Test Summary")
          .expectedOutput("All tests should pass")
          .actualOutput(formatSummaryOutput(totalPassed, totalFailed, totalIgnored))
          .message(formatSummaryMessage(totalPassed, totalFailed, totalIgnored, totalTime))
          .build();
    }
    return null;
  }

  private String formatSummaryOutput(int passed, int failed, int ignored) {
    StringBuilder output = new StringBuilder();
    output.append(String.format("%d test%s passed", passed, passed != 1 ? "s" : ""));
    if (failed > 0) {
      output.append(String.format(", %d failed", failed));
    }
    if (ignored > 0) {
      output.append(String.format(", %d ignored", ignored));
    }
    return output.toString();
  }

  private String formatSummaryMessage(int passed, int failed, int ignored, double time) {
    StringBuilder message = new StringBuilder();
    if (failed == 0 && ignored == 0) {
      message.append(String.format("All %d tests passed successfully", passed));
    } else {
      message.append(String.format("Tests completed: %d passed", passed));
      if (failed > 0) {
        message.append(String.format(", %d failed", failed));
      }
      if (ignored > 0) {
        message.append(String.format(", %d ignored", ignored));
      }
    }
    message.append(String.format(" (%.2fs)", time));
    return message.toString();
  }

  private TestCaseResult createErrorResult(String message) {
    return TestCaseResult.builder()
        .passed(false)
        .input("Test Execution")
        .expectedOutput("Tests should execute successfully")
        .actualOutput("Test execution failed")
        .message(message)
        .build();
  }
}