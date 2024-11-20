package com.cortex.backend.engine.internal.parser;

import com.cortex.backend.engine.api.dto.TestCaseResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaTestResultParser implements TestResultParser {

  private static final Pattern COMPILATION_ERROR_PATTERN = Pattern.compile(
      "\\[ERROR\\] (.+?)(?=\\[INFO\\]|$)",
      Pattern.DOTALL
  );

  private static final Pattern TEST_CLASS_PATTERN = Pattern.compile(
      "\\[INFO\\] Running (\\S+)\\R" +
          "\\[INFO\\] Tests run: (\\d+), Failures: (\\d+), Errors: (\\d+), Skipped: (\\d+), Time elapsed: ([\\d.]+) s(?:.+?in (.+?))?\\R",
      Pattern.DOTALL
  );

  private static final Pattern TEST_METHOD_PATTERN = Pattern.compile(
      "(?:@Test |test)([\\w]+)\\(\\) (?:\\[([\\d.]+) s\\])?"
  );

  @Override
  public List<TestCaseResult> parseTestResults(String output) {
    if (!isValidOutput(output)) {
      return List.of(createErrorResult("No test output provided"));
    }

    String compilationError = findCompilationError(output);
    if (compilationError != null) {
      return List.of(createErrorResult("Compilation failed: " + compilationError));
    }

    List<TestCaseResult> results = new ArrayList<>();
    Matcher classMatcher = TEST_CLASS_PATTERN.matcher(output);

    while (classMatcher.find()) {
      String className = classMatcher.group(1);
      int testsRun = Integer.parseInt(classMatcher.group(2));
      int failures = Integer.parseInt(classMatcher.group(3));
      int errors = Integer.parseInt(classMatcher.group(4));
      int skipped = Integer.parseInt(classMatcher.group(5));
      String timeElapsed = classMatcher.group(6);

      // Primero agregamos el resumen de la clase
      results.add(createClassSummaryResult(
          className, testsRun, failures, errors, skipped, timeElapsed
      ));

      // Si hay fallas o errores, intentamos extraer los detalles
      if (failures > 0 || errors > 0) {
        results.addAll(parseFailureDetails(output, className));
      }
    }

    // Si no encontramos resultados de clase, buscamos un resumen general
    if (results.isEmpty()) {
      TestCaseResult summaryResult = parseOverallSummary(output);
      if (summaryResult != null) {
        results.add(summaryResult);
      }
    }

    return results;
  }


  private List<TestCaseResult> parseFailureDetails(String output, String className) {
    List<TestCaseResult> results = new ArrayList<>();
    Pattern failurePattern = Pattern.compile(
        "(?m)^\\[ERROR\\] ([\\w]+)\\((" + className + ")\\).*?\\R" +
            "(?:Expected: (.+?)\\R)?" +
            "(?:\\s+Actual: (.+?)\\R)?"
    );

    Matcher matcher = failurePattern.matcher(output);
    while (matcher.find()) {
      String methodName = matcher.group(1);
      String expectedOutput = matcher.group(3);
      String actualOutput = matcher.group(4);

      results.add(TestCaseResult.builder()
          .passed(false)
          .input(className + "#" + methodName)
          .expectedOutput(expectedOutput != null ? expectedOutput : "Test should pass")
          .actualOutput(actualOutput != null ? actualOutput : "Test failed")
          .message(String.format("Test '%s' failed", methodName))
          .build());
    }

    return results;
  }


  private boolean isValidOutput(String output) {
    return output != null && !output.trim().isEmpty();
  }

  private String findCompilationError(String output) {
    Matcher matcher = COMPILATION_ERROR_PATTERN.matcher(output);
    if (matcher.find()) {
      return matcher.group(1).trim();
    }
    return null;
  }

  private TestCaseResult createClassSummaryResult(
      String className,
      int testsRun,
      int failures,
      int errors,
      int skipped,
      String timeElapsed) {

    boolean passed = failures == 0 && errors == 0;
    String message = formatSummaryMessage(testsRun, failures, errors, skipped, timeElapsed);

    return TestCaseResult.builder()
        .passed(passed)
        .input(className)
        .expectedOutput("All tests should pass")
        .actualOutput(formatSummaryOutput(testsRun, failures, errors, skipped))
        .message(message)
        .build();
  }

  private String formatSummaryOutput(int testsRun, int failures, int errors, int skipped) {
    return String.format(
        "Tests: %d, Failures: %d, Errors: %d, Skipped: %d",
        testsRun, failures, errors, skipped
    );
  }

  private String formatSummaryMessage(
      int testsRun,
      int failures,
      int errors,
      int skipped,
      String timeElapsed) {

    StringBuilder message = new StringBuilder();

    if (failures == 0 && errors == 0 && skipped == 0) {
      message.append(String.format("All %d tests passed", testsRun));
    } else {
      message.append(String.format(
          "Tests completed with %d failures, %d errors, and %d skipped",
          failures, errors, skipped
      ));
    }

    message.append(String.format(" (%.3fs)", Double.parseDouble(timeElapsed)));

    return message.toString();
  }

  private TestCaseResult parseOverallSummary(String output) {
    Pattern summaryPattern = Pattern.compile(
        "\\[INFO\\] Results:\\R" +
            "\\[INFO\\] \\R" +
            "\\[INFO\\] Tests run: (\\d+), Failures: (\\d+), Errors: (\\d+), Skipped: (\\d+)"
    );

    Matcher matcher = summaryPattern.matcher(output);
    if (matcher.find()) {
      return createClassSummaryResult(
          "Overall Summary",
          Integer.parseInt(matcher.group(1)),
          Integer.parseInt(matcher.group(2)),
          Integer.parseInt(matcher.group(3)),
          Integer.parseInt(matcher.group(4)),
          "0.0"
      );
    }
    return null;
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