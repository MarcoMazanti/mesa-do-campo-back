package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller.Advice;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.External.ErrorResponse;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.External.ReturnModel;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class ModelResponseAdivice implements ResponseBodyAdvice<Object> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public @Nullable Object beforeBodyWrite(@Nullable Object body,
                                            MethodParameter returnType,
                                            MediaType selectedContentType,
                                            Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                            ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) return null;

        if (body instanceof ReturnModel) return body;

        ReturnModel returnModel;

        if (body instanceof ErrorResponse errorResponse) {
            returnModel = new ReturnModel(
                    errorResponse.status(),
                    request.getURI().getPath(),
                    false,
                    1,
                    null,
                    errorResponse
            );
        } else {
            returnModel = new ReturnModel(
                    200,
                    request.getURI().getPath(),
                    true,
                    (body instanceof List<?> lista) ? lista.size() : 1,
                    ((body instanceof List<?> lista) ? lista : Collections.singletonList(body)),
                    null
            );
        }

        if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            try {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return mapper.writeValueAsString(returnModel);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao serializar ReturnModel para JSON", e);
            }
        }

        return returnModel;
    }
}