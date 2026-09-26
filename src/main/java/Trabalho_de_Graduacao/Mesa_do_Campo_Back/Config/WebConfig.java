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
                        // --- ROTAS DO SWAGGER ---
                        "/swagger",
                        "/swagger/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/api-docs",
                        "/api-docs/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",

                        // --- ROTAS PÚBLICAS DA API ---
                        "/api/cliente/all",
                        "/api/produto/all",
                        "/api/vendedor/all",
                        "/api/avaliacao/all",
                        "/api/cliente/create",
                        "/api/cliente/login",
                        "/api/produto/unique/**",
                        "/api/produto/vendedor/**",
                        "/api/produto/categoria/**",
                        "/api/avaliacao/auto",
                        "/api/avaliacao/cliente/**",
                        "/api/avaliacao/unique/**",
                        "/api/avaliacao/vendedor/**"
                );
    }
}
