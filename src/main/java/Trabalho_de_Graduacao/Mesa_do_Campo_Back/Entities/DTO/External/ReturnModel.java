package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Envelope padronizado devolvido pela API para respostas de sucesso e erro.")
public record ReturnModel(
        @Schema(description = "Código HTTP associado à resposta.", example = "200", accessMode = Schema.AccessMode.READ_ONLY)
        Integer status,
        @Schema(description = "Caminho da requisição atendida.", example = "/api/produto/all", accessMode = Schema.AccessMode.READ_ONLY)
        String path,
        @Schema(description = "Indica se a operação foi concluída com sucesso.", example = "true", accessMode = Schema.AccessMode.READ_ONLY)
        boolean success,
        @Schema(description = "Quantidade total de itens retornados antes da paginação. Ausente em respostas de erro.", example = "25", accessMode = Schema.AccessMode.READ_ONLY)
        Integer quantity,
        @Schema(description = "Metadados da paginação em lotes. Ausente em respostas de erro.", implementation = BatchModel.class, accessMode = Schema.AccessMode.READ_ONLY)
        BatchModel batch,
        @Schema(description = "Lista de dados da resposta. O tipo de cada item depende do endpoint chamado; ausente em respostas de erro.", type = "array", example = "[{\"id\": 10}]", accessMode = Schema.AccessMode.READ_ONLY)
        List<?> itens,
        @Schema(description = "Detalhes do erro. Presente somente quando success é false.", implementation = ErrorResponse.class, accessMode = Schema.AccessMode.READ_ONLY)
        ErrorResponse errors) {
    @JsonCreator
    public ReturnModel(@JsonProperty("status") Integer status,
                       @JsonProperty("path") String path,
                       @JsonProperty("success") boolean success,
                       @JsonProperty("quantity") Integer quantity,
                       @JsonProperty("batch") BatchModel batch,
                       @JsonProperty("itens") List<?> itens,
                       @JsonProperty("errors") ErrorResponse errors) {
        this.status = status;
        this.path = path;
        this.success = success;
        this.quantity = quantity;
        this.batch = batch;
        this.itens = itens;
        this.errors = errors;
    }
}
