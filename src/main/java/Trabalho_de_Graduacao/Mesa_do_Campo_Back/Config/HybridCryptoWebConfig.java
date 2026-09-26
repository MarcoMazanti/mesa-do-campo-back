package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class HybridCryptoWebConfig implements WebMvcConfigurer {
    private final HybridCryptoRequestInterceptor hybridCryptoRequestInterceptor;

    public HybridCryptoWebConfig(HybridCryptoRequestInterceptor hybridCryptoRequestInterceptor) {
        this.hybridCryptoRequestInterceptor = hybridCryptoRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hybridCryptoRequestInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/crypto/public-key")
                .order(-100);
    }
}
