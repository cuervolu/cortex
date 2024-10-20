package com.cortex.backend.engine.internal.parser;

import com.cortex.backend.engine.api.dto.TestCaseResult;
import java.util.List;

public interface TestResultParser {
  List<TestCaseResult> parseTestResults(String output);
}