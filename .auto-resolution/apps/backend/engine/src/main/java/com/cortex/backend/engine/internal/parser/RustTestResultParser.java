package com.cortex.backend.engine.internal.parser;

import com.cortex.backend.engine.api.dto.TestCaseResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RustTestResultParser implements TestResultParser {
  @Override
  public List<TestCaseResult> parseTestResults(String output) {
    List<TestCaseResult> results = new ArrayList<>();
    Pattern pattern = Pattern.compile("test (\\S+) ... (\\w+)");
    Matcher matcher = pattern.matcher(output);

    while (matcher.find()) {
      String testName = matcher.group(1);
      boolean passed = "ok".equals(matcher.group(2));
      results.add(TestCaseResult.builder()
          .passed(passed)
          .input(testName)
          .message(passed ? "Test passed" : "Test failed")
          .build());
    }

    return results;
  }
}