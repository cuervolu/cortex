package com.cortex.backend.engine.internal.parser;

import com.cortex.backend.engine.api.dto.TestCaseResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeScriptTestResultParser implements TestResultParser {
  @Override
  public List<TestCaseResult> parseTestResults(String output) {
    List<TestCaseResult> results = new ArrayList<>();
    Pattern pattern = Pattern.compile("Test Files\\s+(\\d+)\\s+passed\\s+\\((\\d+)\\)");
    Matcher matcher = pattern.matcher(output);

    if (matcher.find()) {
      int filesPassed = Integer.parseInt(matcher.group(1));
      int totalFiles = Integer.parseInt(matcher.group(2));
      boolean allPassed = filesPassed == totalFiles;

      results.add(TestCaseResult.builder()
          .passed(allPassed)
          .input("TypeScript Tests")
          .message(allPassed ? String.format("All %d test files passed", totalFiles) :
              String.format("%d out of %d test files passed", filesPassed, totalFiles))
          .build());
    }

    return results;
  }
}