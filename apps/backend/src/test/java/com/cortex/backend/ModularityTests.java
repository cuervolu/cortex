package com.cortex.backend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

  private static final ApplicationModules modules = ApplicationModules.of(Application.class);
  
  @Test
  void verifiesModularity() {
    modules.verify();
  }

}
