@org.springframework.modulith.NamedInterface(name = "User API")
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"auth", "common", "media", "infrastructure"}
)
package com.cortex.backend.user;