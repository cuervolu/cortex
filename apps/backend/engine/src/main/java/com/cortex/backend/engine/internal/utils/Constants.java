package com.cortex.backend.engine.internal.utils;

public final class Constants {
  private Constants() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  // Queue and Redis related constants
  public static final String CODE_EXECUTION_QUEUE = "code_execution";
  public static final String RESULT_KEY_PREFIX = "result:";
  public static final long RESULT_EXPIRATION_HOURS = 1;

  // Directory paths
  public static final String TMP_PREFIX = "code-execution-";
  // Shell related
  public static final String BIN_SH = "/bin/sh";
  

  // File extensions
  public static final String JAVA_EXTENSION = ".java";
  public static final String PYTHON_EXTENSION = ".py";
  public static final String TYPESCRIPT_EXTENSION = ".ts";
  public static final String GO_EXTENSION = ".go";
  public static final String RUST_EXTENSION = ".rs";

  // Config file names
  public static final String POM_XML = "pom.xml";
  public static final String GO_MOD = "go.mod";
  public static final String CARGO_TOML = "Cargo.toml";
  public static final String PACKAGE_JSON = "package.json";
  public static final String TSCONFIG_JSON = "tsconfig.json";
  public static final String PNPM_LOCK = "pnpm-lock.yaml";
  public static final String VITEST_CONFIG = "vitest.config.ts";
  public static final String BIOME_JSON = "biome.json";

  // Source directories
  public static final String JAVA_SRC_DIR = "src/main/java";
  public static final String RUST_SRC_DIR = "src";
}