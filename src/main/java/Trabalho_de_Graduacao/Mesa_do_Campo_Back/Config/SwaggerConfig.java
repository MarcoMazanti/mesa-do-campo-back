package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API da Mesa do Campo")
                        .version("1.0")
                        .description("Documentação do sistema de Mesa do Campo"));
    }
}