@org.springframework.modulith.NamedInterface(name = "Auth API")
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"infrastructure", "common", "user", "media"}
)
package com.cortex.backend.auth;