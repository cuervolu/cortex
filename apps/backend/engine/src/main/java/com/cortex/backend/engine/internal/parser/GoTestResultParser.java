package com.cortex.backend.engine.internal.parser;

import com.cortex.backend.engine.api.dto.TestCaseResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoTestResultParser implements TestResultParser {

  private static final Pattern OK_PATTERN = Pattern.compile("ok\\s+(\\S+)\\s+(\\d+\\.\\d+)s");
  private static final Pattern FAIL_PATTERN = Pattern.compile("FAIL\\s+(\\S+)\\s+(?:\\[([^]]+)])?");
  private static final Pattern TEST_PATTERN = Pattern.compile(
      "--- (PASS|FAIL):\\s+(\\S+)\\s+\\((\\d+\\.\\d+)s\\)");

  private static final String TEST_EXECUTION = "Test execution";
  private static final String EXPECTED_OUTPUT = "All test cases should pass";
  private static final String SUCCESS_OUTPUT = "All test cases passed";
  private static final String FAILURE_OUTPUT = "One or more test cases failed";

  // Mensajes de error y estado
  private static final String NO_OUTPUT_ERROR = "No test output provided";
  private static final String COMPILATION_ERROR = "Compilation failed: %s";
  private static final String PARSE_ERROR = "Unable to parse test output: %s";
  private static final String UNKNOWN_ERROR = "unknown error";

  // Mensajes de formato
  private static final String ALL_TESTS_PASSED_FORMAT = "All tests passed (took %ss)";
  private static final String TESTS_FAILED_FORMAT = "Tests failed: %s";
  private static final String INDIVIDUAL_TEST_FORMAT = "Test %s %s (took %ss)";
  private static final String ALL_TESTS_PASSED = "All tests passed";

  // Estados de test
  private static final String TEST_PASSED = "passed";
  private static final String TEST_FAILED = "failed";
  private static final String TEST_SHOULD_PASS = "Test should pass";
  private static final String TEST_PASSED_MSG = "Test passed";
  private static final String TEST_FAILED_MSG = "Test failed";

  // Indicadores de error de compilación
  private static final String COMPILE_ERROR_INDICATOR = "does not compile";
  private static final String BUILD_ERROR_INDICATOR = "build failed";

  @Override
  public List<TestCaseResult> parseTestResults(String output) {
    if (!isValidOutput(output)) {
      return List.of(createErrorResult(NO_OUTPUT_ERROR));
    }

    if (hasCompilationError(output)) {
      return List.of(createErrorResult("Compilation failed: " + output.trim()));
    }

    List<TestCaseResult> individualTests = parseIndividualTests(output);
    if (!individualTests.isEmpty()) {
      return individualTests;
    }

    return parseTestSummary(output);
  }

  private boolean isValidOutput(String output) {
    return output != null && !output.trim().isEmpty();
  }

  private boolean hasCompilationError(String output) {
    return output.contains(COMPILE_ERROR_INDICATOR) || output.contains(BUILD_ERROR_INDICATOR);
  }

  private List<TestCaseResult> parseIndividualTests(String output) {
    List<TestCaseResult> results = new ArrayList<>();
    Matcher testMatcher = TEST_PATTERN.matcher(output);

    while (testMatcher.find()) {
      results.add(createIndividualTestResult(
          testMatcher.group(1).equals("PASS"),
          testMatcher.group(2),
          testMatcher.group(3)
      ));
    }

    return results;
  }

  private List<TestCaseResult> parseTestSummary(String output) {
    TestCaseResult successResult = parseSuccessfulTests(output);
    if (successResult != null) {
      return List.of(successResult);
    }

    TestCaseResult failureResult = parseFailedTests(output);
    if (failureResult != null) {
      return List.of(failureResult);
    }

    if (output.contains("ok")) {
      return List.of(createSimpleSuccessResult());
    }

    return List.of(createErrorResult("Unable to parse test output: " + output.trim()));
  }

  private TestCaseResult parseSuccessfulTests(String output) {
    Matcher okMatcher = OK_PATTERN.matcher(output);
    if (okMatcher.find()) {
      return createSuccessResult(okMatcher.group(2));
    }
    return null;
  }

  private TestCaseResult parseFailedTests(String output) {
    Matcher failMatcher = FAIL_PATTERN.matcher(output);
    if (failMatcher.find()) {
      return createFailureResult(
          failMatcher.group(2)
      );
    }
    return null;
  }

  private TestCaseResult createIndividualTestResult(boolean passed, String testName,
      String duration) {
    return TestCaseResult.builder()
        .passed(passed)
        .input(testName)
        .message(String.format(INDIVIDUAL_TEST_FORMAT,
            testName,
            passed ? TEST_PASSED : TEST_FAILED,
            duration))
        .expectedOutput(passed ? TEST_SHOULD_PASS : null)
        .actualOutput(passed ? TEST_PASSED_MSG : TEST_FAILED_MSG)
        .build();
  }

  private TestCaseResult createSuccessResult(String duration) {
    return TestCaseResult.builder()
        .passed(true)
        .input(TEST_EXECUTION)
        .message(String.format(ALL_TESTS_PASSED_FORMAT, duration))
        .expectedOutput(EXPECTED_OUTPUT)
        .actualOutput(SUCCESS_OUTPUT)
        .build();
  }

  private TestCaseResult createFailureResult(String errorDetails) {
    return TestCaseResult.builder()
        .passed(false)
        .input(TEST_EXECUTION)
        .message(String.format(TESTS_FAILED_FORMAT,
            errorDetails != null ? errorDetails : UNKNOWN_ERROR))
        .expectedOutput(EXPECTED_OUTPUT)
        .actualOutput(FAILURE_OUTPUT)
        .build();
  }

  private TestCaseResult createSimpleSuccessResult() {
    return TestCaseResult.builder()
        .passed(true)
        .input(TEST_EXECUTION)
        .message(ALL_TESTS_PASSED)
        .expectedOutput(EXPECTED_OUTPUT)
        .actualOutput(SUCCESS_OUTPUT)
        .build();
  }

  private TestCaseResult createErrorResult(String message) {
    return TestCaseResult.builder()
        .passed(false)
        .message(message)
        .build();
  }
}