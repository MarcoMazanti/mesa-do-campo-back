package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.CartaoCredito;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.CartaoDebito;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ChavePix;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MetodoPagamentoDTO(List<CartaoCredito> cartaoCredito, List<CartaoDebito> cartaoDebito, List<ChavePix> chavePix) {
    @JsonCreator
    public MetodoPagamentoDTO(@JsonProperty("cartao_credito") List<CartaoCredito> cartaoCredito,
                              @JsonProperty("cartao_debito") List<CartaoDebito> cartaoDebito,
                              @JsonProperty("chave_pix") List<ChavePix> chavePix) {
        this.cartaoCredito = cartaoCredito;
        this.cartaoDebito = cartaoDebito;
        this.chavePix = chavePix;
    }
}
