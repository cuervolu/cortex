package com.cortex.backend.engine.internal.parser;

import com.cortex.backend.engine.api.dto.TestCaseResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RustTestResultParser implements TestResultParser {

  // Patrones para diferentes partes de la salida
  private static final Pattern TEST_PATTERN = Pattern.compile("test ([^.]+) \\.{3} (ok|FAILED)");
  private static final String ERROR_INDICATOR = "error";
  private static final String WARNING_INDICATOR = "warning";
  private static final int MAX_ERROR_LENGTH = 1000;
  private static final int MAX_ERROR_LINES = 10;
  private static final Pattern SUMMARY_PATTERN = Pattern.compile(
      "test result: (ok|FAILED)\\. (\\d+) passed; (\\d+) failed;");

  // Mensajes constantes
  private static final String COMPILATION_ERROR_PREFIX = "Compilation error: ";
  private static final String TEST_EXECUTION = "Test execution";
  private static final String EXPECTED_OUTPUT = "All tests should pass";
  private static final String SUCCESS_OUTPUT = "All tests passed";
  private static final String FAILURE_OUTPUT = "One or more tests failed";

  @Override
  public List<TestCaseResult> parseTestResults(String output) {
    if (!isValidOutput(output)) {
      return List.of(createErrorResult("No test output provided"));
    }

    String compilationError = findCompilationError(output);
    if (compilationError != null) {
      return List.of(createErrorResult(COMPILATION_ERROR_PREFIX + compilationError.trim()));
    }

    List<TestCaseResult> results = parseIndividualTests(output);

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

  private String findCompilationError(String output) {
    StringBuilder errorMessage = new StringBuilder();
    boolean inError = false;
    int errorLines = 0;

    List<String> significantLines = output.lines()
        .filter(line -> !line.trim().isEmpty())
        .toList();

    for (String line : significantLines) {
      ProcessedLine result = processLine(line, inError, errorLines, errorMessage);
      inError = result.inError();
      errorLines = result.errorLines();

      if (result.shouldStop()) {
        break;
      }
    }

    String error = errorMessage.toString().trim();
    return error.isEmpty() ? null : error;
  }

  private record ProcessedLine(boolean inError, int errorLines, boolean shouldStop) {

  }

  private ProcessedLine processLine(String line, boolean inError, int errorLines,
      StringBuilder errorMessage) {
    // Detectar nuevo error
    if (isNewError(line)) {
      if (inError) {
        errorMessage.append("---\n");
      }
      errorMessage.append(line).append("\n");
      return new ProcessedLine(true, 1, false);
    }

    // Continuar con error existente
    if (inError && errorLines < MAX_ERROR_LINES && isErrorContinuation(line)) {
      errorMessage.append(line).append("\n");
      return new ProcessedLine(true, errorLines + 1,
          errorMessage.length() > MAX_ERROR_LENGTH);
    }

    // Finalizar error actual
    if (inError) {
      if (errorMessage.length() > MAX_ERROR_LENGTH) {
        errorMessage.setLength(MAX_ERROR_LENGTH);
        errorMessage.append("...");
        return new ProcessedLine(false, errorLines, true);
      }
      return new ProcessedLine(false, errorLines, false);
    }

    return new ProcessedLine(false, errorLines, false);
  }

  private boolean isNewError(String line) {
    return line.contains(ERROR_INDICATOR) && !line.contains(WARNING_INDICATOR);
  }

  private boolean isErrorContinuation(String line) {
    return line.startsWith(" ") || line.startsWith("\t");
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
          .expectedOutput(EXPECTED_OUTPUT)
          .actualOutput(passed ? SUCCESS_OUTPUT : FAILURE_OUTPUT)
          .message(passed ?
              String.format("Test '%s' passed successfully", testName) :
              String.format("Test '%s' failed", testName))
          .build());
    }

    return results;
  }

  private TestCaseResult parseSummary(String output) {
    Matcher matcher = SUMMARY_PATTERN.matcher(output);
    if (matcher.find()) {
      boolean passed = "ok".equals(matcher.group(1));
      int passedCount = Integer.parseInt(matcher.group(2));
      int failedCount = Integer.parseInt(matcher.group(3));

      return TestCaseResult.builder()
          .passed(passed)
          .input(TEST_EXECUTION)
          .expectedOutput(EXPECTED_OUTPUT)
          .actualOutput(passed ? SUCCESS_OUTPUT : FAILURE_OUTPUT)
          .message(String.format("Tests completed: %d passed, %d failed",
              passedCount, failedCount))
          .build();
    }
    return null;
  }

  private TestCaseResult createErrorResult(String message) {
    return TestCaseResult.builder()
        .passed(false)
        .input(TEST_EXECUTION)
        .message(message)
        .expectedOutput(EXPECTED_OUTPUT)
        .actualOutput(FAILURE_OUTPUT)
        .build();
  }
}