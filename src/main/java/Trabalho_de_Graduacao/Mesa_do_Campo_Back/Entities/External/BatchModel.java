package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.External;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Metadados de paginação por lotes presentes no envelope de resposta.")
public record BatchModel(
        @Schema(description = "Número máximo de itens em cada lote.", example = "10", accessMode = Schema.AccessMode.READ_ONLY)
        Integer quantity,
        @Schema(description = "Número do lote retornado, iniciando em 1.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Integer loteAtual,
        @Schema(description = "Quantidade total de lotes disponíveis.", example = "3", accessMode = Schema.AccessMode.READ_ONLY)
        Integer loteTotal) {
    @JsonCreator
    public BatchModel(@JsonProperty("quantity") Integer quantity,
                      @JsonProperty("loteAtual") Integer loteAtual,
                      @JsonProperty("loteTotal") Integer loteTotal) {
        this.quantity = quantity;
        this.loteAtual = loteAtual;
        this.loteTotal = loteTotal;
    }
}
