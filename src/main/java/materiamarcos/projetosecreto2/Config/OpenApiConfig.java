package materiamarcos.projetosecreto2.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API da Farmácia BARATEIRA",
                version = "v1",
                description = "Documentação dos endpoints da API para o sistema de gerenciamento de farmácias."
        ),
        security = @SecurityRequirement(name = "bearerAuth") // Aplica segurança JWT a todos os endpoints
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Token JWT para autenticação. Insira 'Bearer ' antes do seu token.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // Esta classe existe apenas para configuração do Swagger/OpenAPI.
}