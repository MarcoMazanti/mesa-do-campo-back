package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados públicos de um cliente, sem a senha. É o formato retornado por cadastro, login e consultas de cliente.")
public record ClienteDTO(
        @Schema(description = "Identificador único do cliente.", example = "1") int id,
        @Schema(description = "Nome completo — também usado como usuário no Basic Auth.", example = "Maria da Silva") String nome,
        @Schema(description = "CPF ou CNPJ, apenas números.", example = "12345678900") String cpfOrCnpj,
        @Schema(description = "E-mail cadastrado, usado para login.", example = "maria@email.com") String email,
        @Schema(description = "Telefone.", example = "14999998888") String telefone,
        @Schema(description = "ID do endereço de entrega vinculado, ou nulo se ainda não houver um.", example = "3", nullable = true) Integer idEnderecoEntrega) {
    @JsonCreator
    public ClienteDTO(@JsonProperty("id") int id,
                      @JsonProperty("nome") String nome,
                      @JsonProperty("cpfOrCnpj") String cpfOrCnpj,
                      @JsonProperty("email") String email,
                      @JsonProperty("telefone") String telefone,
                      @JsonProperty("idEnderecoEntrega") Integer idEnderecoEntrega) {
        this.id = id;
        this.nome = nome;
        this.cpfOrCnpj = cpfOrCnpj;
        this.email = email;
        this.telefone = telefone;
        this.idEnderecoEntrega = idEnderecoEntrega;
    }
}
