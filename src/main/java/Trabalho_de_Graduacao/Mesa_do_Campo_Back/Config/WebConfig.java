package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private Interceptador interceptador;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptador)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/swagger",
                        "/swagger-ui/**",
                        "/api-docs/**",
                        "/v3/api-docs/**",
                        "/**/all",
                        "/api/cliente/create",
                        "/api/cliente/login"
                );
    }
}
