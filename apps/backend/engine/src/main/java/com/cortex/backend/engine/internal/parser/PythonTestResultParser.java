package com.cortex.backend.engine.internal.parser;

import com.cortex.backend.engine.api.dto.TestCaseResult;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonTestResultParser implements TestResultParser {
  @Override
  public List<TestCaseResult> parseTestResults(String output) {
    // Combina stdout y stderr para el análisis
    String combinedOutput = output;

    boolean passed = combinedOutput.contains("OK");
    Pattern pattern = Pattern.compile("Ran (\\d+) test");
    Matcher matcher = pattern.matcher(combinedOutput);

    String testCount = matcher.find() ? matcher.group(1) : "unknown";

    return List.of(TestCaseResult.builder()
        .passed(passed)
        .input("Python Tests")
        .message(passed ?
            String.format("All %s tests passed", testCount) :
            String.format("%s tests run", testCount))
        .build());
  }
}