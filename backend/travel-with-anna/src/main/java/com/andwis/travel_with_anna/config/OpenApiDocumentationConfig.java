package com.andwis.travel_with_anna.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = OpenApiDocumentationConfig.API_TITLE,
                description = OpenApiDocumentationConfig.API_DESCRIPTION,
                version = OpenApiDocumentationConfig.API_VERSION
        ),
        servers = @Server(
                description = OpenApiDocumentationConfig.SERVER_DESCRIPTION,
                url = OpenApiDocumentationConfig.SERVER_URL
        ),
        security = @SecurityRequirement(name = OpenApiDocumentationConfig.SECURITY_SCHEME_NAME)
)
@SecurityScheme(
        name = OpenApiDocumentationConfig.SECURITY_SCHEME_NAME,
        scheme = OpenApiDocumentationConfig.SECURITY_SCHEME_TYPE,
        type = SecuritySchemeType.HTTP,
        bearerFormat = OpenApiDocumentationConfig.BEARER_FORMAT,
        in = SecuritySchemeIn.HEADER
)
public class OpenApiDocumentationConfig {
        public static final String API_TITLE = "OpenApi specification for Travel with Anna API";
        public static final String API_DESCRIPTION = "OpenApi documentation for Travel with Anna API";
        public static final String API_VERSION = "v1";

        public static final String SERVER_DESCRIPTION = "Local server";
        public static final String SERVER_URL = "http://localhost:8080/twa/v1";

        public static final String SECURITY_SCHEME_NAME = "bearerAuth";
        public static final String SECURITY_SCHEME_TYPE = "bearer";
        public static final String BEARER_FORMAT = "JWT";
}
