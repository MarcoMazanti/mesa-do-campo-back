package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public final class HybridCryptoContext {
    public static final String ENCRYPTED_KEY_HEADER = "X-Hybrid-Encrypted-Key";
    private static final String REQUIRED_ATTRIBUTE = HybridCryptoContext.class.getName() + ".required";
    private static final String AES_KEY_ATTRIBUTE = HybridCryptoContext.class.getName() + ".aesKey";

    private HybridCryptoContext() {
    }

    public static void require() {
        attributes().setAttribute(REQUIRED_ATTRIBUTE, true, RequestAttributes.SCOPE_REQUEST);
    }

    public static boolean isRequired() {
        return Boolean.TRUE.equals(attributes().getAttribute(REQUIRED_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST));
    }

    public static void setAesKey(SecretKey aesKey) {
        attributes().setAttribute(AES_KEY_ATTRIBUTE, aesKey, RequestAttributes.SCOPE_REQUEST);
    }

    public static SecretKey getAesKey() {
        return (SecretKey) attributes().getAttribute(AES_KEY_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    }

    public static byte[] requestAad() {
        HttpServletRequest request = servletRequest();
        return (request.getMethod() + ":" + request.getRequestURI()).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] responseAad() {
        HttpServletRequest request = servletRequest();
        return ("RESPONSE:" + request.getMethod() + ":" + request.getRequestURI()).getBytes(StandardCharsets.UTF_8);
    }

    private static RequestAttributes attributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("O contexto da requisição não está disponível.");
        }
        return attributes;
    }

    private static HttpServletRequest servletRequest() {
        RequestAttributes attributes = attributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            throw new IllegalStateException("A requisição atual não é HTTP.");
        }
        return servletAttributes.getRequest();
    }
}
