package com.cortex.backend.engine.internal.parser;

import com.cortex.backend.engine.api.dto.TestCaseResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeScriptTestResultParser implements TestResultParser {
  private static final Pattern TEST_FILE_PATTERN = Pattern.compile(
      "✓\\s+([^\\s].+?)\\s+\\((\\d+)\\s+tests?\\)\\s+(\\d+)ms"
  );

  private static final Pattern SUMMARY_PATTERN = Pattern.compile(
      "Test Files\\s+(\\d+)\\s+passed\\s+\\((\\d+)\\)\\R" +
          "\\s+Tests\\s+(\\d+)\\s+passed\\s+\\((\\d+)\\)"
  );

  private static final Pattern INDIVIDUAL_TEST_PATTERN = Pattern.compile(
      "✓\\s+([^\\n]+?)\\s+\\((\\d+)ms\\)"
  );

  private static final Pattern FAILED_TEST_PATTERN = Pattern.compile(
      "❌\\s+([^\\n]+?)\\s+\\((\\d+)ms\\)\\R+(?:Error: )?([^\\n]+)"
  );

  @Override
  public List<TestCaseResult> parseTestResults(String output) {
    if (!isValidOutput(output)) {
      return List.of(createErrorResult("No test output provided"));
    }

    List<TestCaseResult> results = new ArrayList<>();

    // Primero buscar errores de compilación
    if (output.contains("Failed to compile")) {
      return List.of(createErrorResult("TypeScript compilation failed"));
    }

    // Procesar archivos de prueba
    Matcher filesMatcher = TEST_FILE_PATTERN.matcher(output);
    while (filesMatcher.find()) {
      String fileName = filesMatcher.group(1);
      int testCount = Integer.parseInt(filesMatcher.group(2));
      String duration = filesMatcher.group(3);

      results.add(createFileResult(fileName, testCount, duration));
    }

    // Buscar pruebas individuales exitosas
    Matcher testMatcher = INDIVIDUAL_TEST_PATTERN.matcher(output);
    while (testMatcher.find()) {
      String testName = testMatcher.group(1).trim();
      String duration = testMatcher.group(2);

      results.add(createTestResult(testName, true, duration, null));
    }

    // Buscar pruebas fallidas
    Matcher failedMatcher = FAILED_TEST_PATTERN.matcher(output);
    while (failedMatcher.find()) {
      String testName = failedMatcher.group(1).trim();
      String duration = failedMatcher.group(2);
      String errorMessage = failedMatcher.group(3);

      results.add(createTestResult(testName, false, duration, errorMessage));
    }

    // Agregar resumen general si hay resultados
    Matcher summaryMatcher = SUMMARY_PATTERN.matcher(output);
    if (summaryMatcher.find()) {
      results.add(createSummaryResult(
          Integer.parseInt(summaryMatcher.group(1)), // files passed
          Integer.parseInt(summaryMatcher.group(2)), // total files
          Integer.parseInt(summaryMatcher.group(3)), // tests passed
          Integer.parseInt(summaryMatcher.group(4))  // total tests
      ));
    }

    return results;
  }

  private boolean isValidOutput(String output) {
    return output != null && !output.trim().isEmpty();
  }

  private TestCaseResult createFileResult(String fileName, int testCount, String duration) {
    return TestCaseResult.builder()
        .passed(true)
        .input(fileName)
        .expectedOutput("All tests should pass")
        .actualOutput(String.format("%d tests executed", testCount))
        .message(String.format("File '%s' passed %d tests (%sms)",
            fileName, testCount, duration))
        .build();
  }

  private TestCaseResult createTestResult(String testName, boolean passed, String duration, String error) {
    return TestCaseResult.builder()
        .passed(passed)
        .input(testName)
        .expectedOutput("Test should pass")
        .actualOutput(passed ? "Passed" : error)
        .message(String.format("Test '%s' %s (%sms)",
            testName,
            passed ? "passed" : "failed: " + error,
            duration))
        .build();
  }

  private TestCaseResult createSummaryResult(int filesPassed, int totalFiles,
      int testsPassed, int totalTests) {
    boolean allPassed = filesPassed == totalFiles && testsPassed == totalTests;

    return TestCaseResult.builder()
        .passed(allPassed)
        .input("Test Summary")
        .expectedOutput("All tests should pass")
        .actualOutput(String.format(
            "Files: %d/%d passed, Tests: %d/%d passed",
            filesPassed, totalFiles, testsPassed, totalTests))
        .message(String.format(
            "%d/%d test files and %d/%d individual tests passed",
            filesPassed, totalFiles, testsPassed, totalTests))
        .build();
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