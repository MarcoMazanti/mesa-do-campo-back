package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.External;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Detalhe do erro incluído no campo errors do envelope de resposta.")
public record ErrorResponse(
        @Schema(description = "Código HTTP do erro.", example = "404", accessMode = Schema.AccessMode.READ_ONLY)
        Integer status,
        @Schema(description = "Mensagem que descreve a causa do erro.", example = "Registro não encontrado.", accessMode = Schema.AccessMode.READ_ONLY)
        String message,
        @Schema(description = "Data e hora em que o erro foi gerado, no formato ISO-8601.", format = "date-time", example = "2026-09-25T21:50:00", accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime hour) {
    @JsonCreator
    public ErrorResponse(@JsonProperty("status") Integer status,
                         @JsonProperty("message") String message,
                         @JsonProperty("hour") LocalDateTime hour) {
        this.status = status;
        this.message = message;
        this.hour = hour;
    }
}
