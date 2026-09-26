package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Endereço cadastrado por um usuário. Pode ser usado como endereço de entrega (Cliente.idEnderecoEntrega) ou, no caso de um vendedor, como referência de onde ele vende seus produtos.")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "endereco")
public class Endereco {
    @Schema(description = "Identificador único do endereço, gerado pelo banco.", example = "3", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Schema(description = "ID do usuário (cliente) dono deste endereço. Sempre sobrescrito pelo id do usuário autenticado ao criar.", example = "1")
    @NotNull
    @Column(name = "id_usuario", nullable = false)
    private int idUsuario;

    @Schema(description = "CEP, apenas números (8 dígitos). Pontuação é removida automaticamente.", example = "19910000")
    @NotBlank
    @Size(min = 8, max = 8, message = "Tamanho Inesperado para o CEP.")
    @Column(name = "cep", nullable = false)
    private String cep;

    @Schema(description = "País.", example = "Brasil")
    @Size(max = 255)
    @Column(name = "country")
    private String country;

    @Schema(description = "Estado (UF).", example = "SP")
    @Size(max = 255)
    @Column(name = "state")
    private String state;

    @Schema(description = "Cidade.", example = "Ourinhos")
    @Size(max = 255)
    @Column(name = "city")
    private String city;

    @Schema(description = "Logradouro (rua/avenida).", example = "Rua Alberto Zunta")
    @Size(max = 255)
    @Column(name = "adress")
    private String adress;

    @Schema(description = "Número do imóvel.", example = "493")
    @Column(name = "number")
    private int number;

    @Schema(description = "Complemento (apto, bloco, referência etc.), opcional.", example = "Apto 12")
    @Size(max = 255)
    @Column(name = "complement")
    private String complement;

    public Endereco(int id, int idUsuario, String cep, String country, String state, String city, String adress, int number) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.cep = cep;
        this.country = country;
        this.state = state;
        this.city = city;
        this.adress = adress;
        this.number = number;
    }

    public Endereco(int idUsuario, String cep) {
        this.idUsuario = idUsuario;
        this.cep = cep;
    }

    public void setCep(String cep) {
        this.cep = cep.replaceAll("\\D", "");
    }
}
