package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Dados públicos de um vendedor, combinando informações da conta de vendedor com o nome/e-mail/telefone do cliente correspondente.")
public record VendedorDTO(
        @Schema(description = "ID do vendedor (igual ao id do cliente).", example = "1") int idVendedor,
        @Schema(description = "Nome do cliente/vendedor.", example = "Maria da Silva") String nome,
        @Schema(description = "E-mail do cliente/vendedor.", example = "maria@email.com") String email,
        @Schema(description = "Telefone do cliente/vendedor.", example = "14999998888") String telefone,
        @Schema(description = "Nota média das avaliações recebidas.", example = "4.5") float avaliacao,
        @Schema(description = "Data de cadastro como vendedor.", example = "2026-01-15") LocalDate dataAdmissao) {
    @JsonCreator
    public VendedorDTO(@JsonProperty("idVendedor") int idVendedor,
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
