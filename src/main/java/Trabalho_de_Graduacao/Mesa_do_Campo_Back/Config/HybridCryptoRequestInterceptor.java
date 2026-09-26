package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Config;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.CryptoService;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.HybridCryptoContext;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.HybridEncrypted;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/** Define a política criptográfica antes de o controller ou um advice serem executados. */
@Component
public class HybridCryptoRequestInterceptor implements HandlerInterceptor {
    private final CryptoService cryptoService;

    public HybridCryptoRequestInterceptor(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod) || !isHybridEncrypted(handlerMethod)) {
            return true;
        }

        HybridCryptoContext.require();
        String wrappedKey = request.getHeader(HybridCryptoContext.ENCRYPTED_KEY_HEADER);
        if (wrappedKey != null && !wrappedKey.isBlank()) {
            HybridCryptoContext.setAesKey(cryptoService.descriptografarChaveAES(wrappedKey));
        }
        return true;
    }

    private boolean isHybridEncrypted(HandlerMethod handlerMethod) {
        return AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), HybridEncrypted.class)
                || AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), HybridEncrypted.class);
    }
}
