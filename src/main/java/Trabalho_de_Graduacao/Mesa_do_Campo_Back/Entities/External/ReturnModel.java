package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.External;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReturnModel(
        Integer status,
        String path,
        boolean success,
        Integer quantity,
        BatchModel batch,
        List<?> itens,
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