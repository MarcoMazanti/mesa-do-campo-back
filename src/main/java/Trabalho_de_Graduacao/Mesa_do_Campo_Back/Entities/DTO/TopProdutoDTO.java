package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TopProdutoDTO(int id, String nome, int quantidade, double valorVendido, float percentual) {
    @JsonCreator
    public TopProdutoDTO(@JsonProperty("id") int id,
                         @JsonProperty("nome") String nome,
                         @JsonProperty("quant_vendida") int quantidade,
                         @JsonProperty("valor_vendido") double valorVendido,
                         @JsonProperty("percentual") float percentual) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.valorVendido = valorVendido;
        this.percentual = percentual;
    }
}
