package com.cortex.backend.engine.internal.parser;

import com.cortex.backend.engine.api.dto.TestCaseResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonTestResultParser implements TestResultParser {

  private static final Pattern TEST_PATTERN = Pattern.compile(
      "(\\w+)\\s+\\((.*?)\\)\\s+\\.{3}\\s+(ok|FAIL|ERROR|skipped)"
  );
  private static final Pattern SUMMARY_PATTERN = Pattern.compile(
      "Ran (\\d+) tests? in ([\\d.]+)s\n\n(OK|FAILED)(.*?)?$",
      Pattern.DOTALL
  );
  private static final Pattern ERROR_PATTERN = Pattern.compile(
      "={70}\nERROR: (.*?)\n-{70}\n(.*?)(?=\n={70}|\n-{70}|$)",
      Pattern.DOTALL
  );

  @Override
  public List<TestCaseResult> parseTestResults(String output) {
    if (!isValidOutput(output)) {
      return List.of(createErrorResult("No test output provided"));
    }


    String error = findError(output);
    if (error != null) {
      return List.of(createErrorResult("Test execution failed: " + error));
    }

    List<TestCaseResult> results = new ArrayList<>();
    
    Matcher testMatcher = TEST_PATTERN.matcher(output);
    while (testMatcher.find()) {
      results.add(createTestResult(
          testMatcher.group(1),    // nombre del test
          testMatcher.group(2),    // clase del test
          testMatcher.group(3)     // resultado
      ));
    }

    // Si no se encontraron pruebas individuales, crear resultado general
    if (results.isEmpty()) {
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

  private String findError(String output) {
    Matcher errorMatcher = ERROR_PATTERN.matcher(output);
    if (errorMatcher.find()) {
      String errorType = errorMatcher.group(1).trim();
      String errorDetails = errorMatcher.group(2).trim();
      return String.format("%s: %s", errorType, errorDetails);
    }
    return null;
  }

  private TestCaseResult createTestResult(String testName, String testClass, String result) {
    boolean passed = "ok".equals(result);
    String fullTestName = String.format("%s.%s", testClass, testName);

    return TestCaseResult.builder()
        .passed(passed)
        .input(fullTestName)
        .expectedOutput(passed ? "Test should pass" : "Test failed")
        .actualOutput(result)
        .message(formatTestMessage(testName, result))
        .build();
  }

  private String formatTestMessage(String testName, String result) {
    return switch (result.toLowerCase()) {
      case "ok" -> String.format("Test '%s' passed successfully", testName);
      case "fail" -> String.format("Test '%s' failed", testName);
      case "error" -> String.format("Test '%s' encountered an error", testName);
      case "skipped" -> String.format("Test '%s' was skipped", testName);
      default -> String.format("Test '%s' had unexpected result: %s", testName, result);
    };
  }

  private TestCaseResult parseSummary(String output) {
    Matcher summaryMatcher = SUMMARY_PATTERN.matcher(output);
    if (summaryMatcher.find()) {
      int testCount = Integer.parseInt(summaryMatcher.group(1));
      String duration = summaryMatcher.group(2);
      boolean passed = "OK".equals(summaryMatcher.group(3));
      String details = summaryMatcher.group(4);

      return TestCaseResult.builder()
          .passed(passed)
          .input("Test Summary")
          .expectedOutput("All tests should pass")
          .actualOutput(passed ? "All tests passed" : "Some tests failed")
          .message(formatSummaryMessage(testCount, duration, passed, details))
          .build();
    }
    return null;
  }

  private String formatSummaryMessage(int testCount, String duration, boolean passed,
      String details) {
    StringBuilder message = new StringBuilder()
        .append(String.format("Ran %d tests in %ss", testCount, duration));

    if (!passed && details != null && !details.trim().isEmpty()) {
      message.append(" - ").append(details.trim());
    }

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