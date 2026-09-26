package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller.Advice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.IOException;
import java.io.InputStream;

final class CustomHttpInputMessage implements HttpInputMessage {
    private final InputStream body;
    private final HttpHeaders headers;

    CustomHttpInputMessage(InputStream body, HttpHeaders headers) {
        this.body = body;
        this.headers = headers;
    }

    @Override
    public InputStream getBody() throws IOException {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
