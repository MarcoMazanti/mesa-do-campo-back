package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller.Advice;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External.EnvelopeResponseDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.CryptoException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.CryptoService;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.HybridCryptoContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;

/** Última etapa da resposta: criptografa o modelo já normalizado pelo ModelResponseAdvice. */
@Order(Ordered.LOWEST_PRECEDENCE)
@ControllerAdvice
public class EncryptResponseInterceptor implements ResponseBodyAdvice<Object> {
    private final CryptoService cryptoService;
    private final ObjectMapper objectMapper;

    public EncryptResponseInterceptor(CryptoService cryptoService, ObjectMapper objectMapper) {
        this.cryptoService = cryptoService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // A decisão depende da rota original; ela é registrada pelo interceptor MVC,
        // inclusive quando a resposta vem de um @ExceptionHandler.
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (!HybridCryptoContext.isRequired() || body == null || body instanceof EnvelopeResponseDTO) {
            return body;
        }

        SecretKey aesKey = HybridCryptoContext.getAesKey();
        if (aesKey == null) {
            // Sem chave de sessão não existe uma forma segura de devolver o erro cifrado.
            // O GlobalExceptionHandler então preserva a resposta 400 em JSON normal.
            return body;
        }

        try {
            String jsonPlano = objectMapper.writeValueAsString(body);
            EnvelopeResponseDTO envelope = cryptoService.criptografarResposta(
                    jsonPlano, aesKey, HybridCryptoContext.responseAad());
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
                return objectMapper.writeValueAsString(envelope);
            }
            return envelope;
        } catch (CryptoException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoException("Falha ao serializar a resposta criptografada.", e);
        }
    }
}
