package com.cortex.backend.engine.internal.parser;

public class TestResultParserFactory {
  public static TestResultParser getParser(String language) {
    return switch (language.toLowerCase()) {
      case "rust" -> new RustTestResultParser();
      case "java" -> new JavaTestResultParser();
      case "typescript" -> new TypeScriptTestResultParser();
      case "go" -> new GoTestResultParser();
      case "python" -> new PythonTestResultParser();
      default -> throw new IllegalArgumentException("Unsupported language: " + language);
    };
  }
}