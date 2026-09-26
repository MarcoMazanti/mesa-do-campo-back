package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller.Advice;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External.EnvelopeRequestDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.CryptoException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.CryptoService;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.HybridCryptoContext;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.HybridEncrypted;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/** Desempacota requests de endpoints marcados com {@link HybridEncrypted}. */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class DecryptRequestInterceptor extends RequestBodyAdviceAdapter {
    private final CryptoService cryptoService;
    private final ObjectMapper objectMapper;

    public DecryptRequestInterceptor(CryptoService cryptoService, ObjectMapper objectMapper) {
        this.cryptoService = cryptoService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter parameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return isHybridEncrypted(parameter);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                            Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        try {
            EnvelopeRequestDTO envelope = objectMapper.readValue(inputMessage.getBody(), EnvelopeRequestDTO.class);
            validateProtocol(envelope);

            SecretKey aesKey = cryptoService.descriptografarChaveAES(envelope.getEncryptedKey());
            String jsonOriginal = cryptoService.descriptografarDados(
                    envelope.getEncryptedData(), envelope.getIv(), aesKey, HybridCryptoContext.requestAad());
            HybridCryptoContext.setAesKey(aesKey);

            HttpHeaders headers = new HttpHeaders();
            headers.putAll(inputMessage.getHeaders());
            headers.setContentLength(jsonOriginal.getBytes(StandardCharsets.UTF_8).length);
            return new CustomHttpInputMessage(
                    new ByteArrayInputStream(jsonOriginal.getBytes(StandardCharsets.UTF_8)), headers);
        } catch (CryptoException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoException("Envelope híbrido inválido.", e);
        }
    }

    private boolean isHybridEncrypted(MethodParameter parameter) {
        return AnnotatedElementUtils.hasAnnotation(parameter.getMethod(), HybridEncrypted.class)
                || AnnotatedElementUtils.hasAnnotation(parameter.getContainingClass(), HybridEncrypted.class);
    }

    private void validateProtocol(EnvelopeRequestDTO envelope) {
        if (envelope == null || !"v1".equals(envelope.getVersion())
                || !CryptoService.ALGORITHM.equals(envelope.getAlgorithm())) {
            throw new CryptoException("Versão ou algoritmo do envelope não suportado.");
        }
    }
}
