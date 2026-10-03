package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record VendasMensaisDTO(int ano, int mes, double valorVendido) {
    @JsonCreator
    public VendasMensaisDTO(@JsonProperty("ano") int ano,
                            @JsonProperty("mes") int mes,
                            @JsonProperty("valor_vendido") double valorVendido) {
        this.ano = ano;
        this.mes = mes;
        this.valorVendido = valorVendido;
    }
}
