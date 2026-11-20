package com.ursia.tavalendo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação OpenAPI/Swagger.
 * Define o esquema de autenticação Bearer JWT usado pela maioria dos endpoints.
 */
@Configuration
public class ConfiguracaoSwagger {

    @Bean
    public OpenAPI tavalendoOpenAPI() {
        String schemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("TaValendo API")
                        .description("Sistema de controle de validade de produtos")
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}