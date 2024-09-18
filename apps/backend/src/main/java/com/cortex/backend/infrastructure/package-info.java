@org.springframework.modulith.NamedInterface(name = "Infrastructure API")
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"auth", "common", "user"}
)
package com.cortex.backend.infrastructure;