package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record VendedorDTO(int idVendedor, String nome, String email, String telefone, float avaliacao, LocalDate dataAdmissao) {
    @JsonCreator
    public VendedorDTO(@JsonProperty("id_vendedor") int idVendedor,
                       @JsonProperty("nome") String nome,
                       @JsonProperty("email") String email,
                       @JsonProperty("telefone") String telefone,
                       @JsonProperty("avaliacao") float avaliacao,
                       @JsonProperty("dataAdmissao") LocalDate dataAdmissao) {
        this.idVendedor = idVendedor;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.avaliacao = avaliacao;
        this.dataAdmissao = dataAdmissao;
    }
}
