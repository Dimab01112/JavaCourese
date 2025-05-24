package com.example.ECommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.Scopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:http://localhost:8081/realms/ecommerce}")
    private String issuerUri;

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "oauth2";
        return new OpenAPI()
            .info(new Info()
                .title("ECommerce API")
                .description("REST API for ECommerce application")
                .version("1.0.0")
                .contact(new Contact()
                    .name("ECommerce Team")
                    .email("support@ecommerce.com")
                    .url("https://ecommerce.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(new OAuthFlows()
                            .authorizationCode(new OAuthFlow()
                                .authorizationUrl("http://localhost:8081/realms/ecommerce/protocol/openid-connect/auth")
                                .tokenUrl("http://localhost:8081/realms/ecommerce/protocol/openid-connect/token")
                                .scopes(new Scopes()
                                    .addString("openid", "OpenID Connect")
                                    .addString("profile", "User profile")
                                    .addString("email", "User email")
                                    .addString("roles", "User roles")
                                    .addString("offline_access", "Offline access"))))));
    }
} 