package net.project.journalApp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {

                final String securitySchemeName = "BearerAuth";

                Server localServer = new Server()
                                .url("http://localhost:8080")
                                .description("Local development server");

                Server prodServer = new Server()
                                .url("https://api.yourdomain.com")
                                .description("Production server");

                return new OpenAPI()
                                .info(new Info()
                                                .title("Journal App API")
                                                .description("API documentation for the Journal App backend")
                                                .version("1.0.0"))
                                .servers(Arrays.asList(localServer, prodServer))
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // Global apply
                                .components(new Components()
                                        .addSecuritySchemes(securitySchemeName,
                                                new SecurityScheme()
                                                        .name(securitySchemeName)
                                                        .type(SecurityScheme.Type.HTTP)
                                                        .scheme("bearer")
                                                        .bearerFormat("JWT") // Optional: shows "JWT" as hint
                                        ));
        }
}
