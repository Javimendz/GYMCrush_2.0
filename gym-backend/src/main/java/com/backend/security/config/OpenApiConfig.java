package com.backend.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * Configuración de OpenAPI (Swagger) para la documentación de la API.
 * <p>
 * Esta clase configura la documentación interactiva de la API RESTful
 * utilizando OpenAPI 3. Incluye:
 * <ul>
 *   <li>Información general del proyecto (título, versión, descripción)</li>
 *   <li>Datos de contacto y licencia</li>
 *   <li>Configuración de autenticación JWT para probar endpoints protegidos</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Configuration
public class OpenApiConfig {

    /** Nombre del esquema de seguridad para autenticación Bearer */
    private static final String SCHEME_NAME = "bearerAuth";
    /** Formato del token Bearer (JWT) */
    private static final String BEARER_FORMAT = "JWT";
    /** Descripción del esquema de autenticación */
    private static final String DESCRIPTION = "Bearer authentication para JWT token";

    /**
     * Configura y personaliza la documentación OpenAPI de la API.
     * <p>
     * Añade autenticación JWT para que los usuarios puedan probar endpoints
     * protegidos directamente desde la interfaz de Swagger UI.
     * </p>
     *
     * @return la configuración personalizada de OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Añade el requisito de seguridad JWT para todos los endpoints protegidos
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new Components()
                        // Configura el esquema de seguridad Bearer con JWT
                        .addSecuritySchemes(SCHEME_NAME, new SecurityScheme().name(SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat(BEARER_FORMAT)
                                .description(DESCRIPTION)
                                .in(SecurityScheme.In.HEADER)

                        ))
                // Información general de la API
                .info(new Info()
                        .title("API para GymCrush - TFG")
                        .version("1.0")
                        .description("Documentación de la API Restful para GymCrush. Proyecto de fin de grado.")
                        .contact(new Contact()
                                .name("GymCrush")
                                .email("gymcrush@example.com")
                                .url("https://www.gymcrush.com"))
                        .license(new License()

                                .name("Apache 2.0 License")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")

                        ));

    }
}
