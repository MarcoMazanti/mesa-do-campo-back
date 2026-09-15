package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.External;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BatchModel(
        Integer quantity,
        Integer loteAtual,
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
