@org.springframework.modulith.NamedInterface(name = "Common API")
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"infrastructure", "handler"}
)
package com.cortex.backend.core.common;