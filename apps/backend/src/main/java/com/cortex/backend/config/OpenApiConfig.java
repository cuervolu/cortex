package com.cortex.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        contact = @Contact(
            name = "Cortex Team",
            email = "team@cuevolu.dev",
            url = "https://github.com/cuervolu/cortex"
        ),
        description = "OpenAPI documentation for the Cortex API",
        title = "Cortex OpenAPI Specification",
        version = "1.0"
    ),
    servers = {
        @Server(
            url = "http://localhost:8088/api/v1",
            description = "Local Environment"
        ),
        @Server(
            url = "https://api.cortex.com/api/v1",
            description = "Production Environment"
        )
    },
    security = {
        @SecurityRequirement(name = "bearerAuth")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT Bearer Token",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
@SecurityScheme(
    name = "oauth2-google",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
        authorizationCode = @OAuthFlow(
            authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth",
            tokenUrl = "https://oauth2.googleapis.com/token",
            scopes = {
                @OAuthScope(name = "openid", description = "OpenID Connect scope"),
                @OAuthScope(name = "profile", description = "Access to user's basic profile information"),
                @OAuthScope(name = "email", description = "Access to user's email address")
            }
        )
    )
)
@SecurityScheme(
    name = "oauth2-github",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
        authorizationCode = @OAuthFlow(
            authorizationUrl = "https://github.com/login/oauth/authorize",
            tokenUrl = "https://github.com/login/oauth/access_token",
            scopes = {
                @OAuthScope(name = "user", description = "Read access to a user's profile info"),
                @OAuthScope(name = "user:email", description = "Read access to a user's email addresses")
            }
        )
    )
)
public class OpenApiConfig {

}
