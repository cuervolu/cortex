package com.cortex.backend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

  private static final ApplicationModules modules = ApplicationModules.of(Application.class);
  
  @Test
  void verifiesModularity() {
    modules.verify();
  }

  @Test
  void createModuleDocumentation() {
    ApplicationModules modules = ApplicationModules.of(Application.class);
    new Documenter(modules)
        .writeDocumentation()
        .writeIndividualModulesAsPlantUml();
  }

}
