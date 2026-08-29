package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "endereco")
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Column(name = "id_usuario", nullable = false)
    private int idUsuario;

    @NotBlank
    @Size(min = 8, max = 8, message = "Tamanho Inesperado para o CEP.")
    @Column(name = "cep", nullable = false)
    private String cep;

    @Size(max = 255)
    @Column(name = "country")
    private String country;

    @Size(max = 255)
    @Column(name = "state")
    private String state;

    @Size(max = 255)
    @Column(name = "city")
    private String city;

    @Size(max = 255)
    @Column(name = "adress")
    private String adress;

    @Column(name = "number")
    private int number;

    @Size(max = 255)
    @Column(name = "complement")
    private String complement;

    /*
     * Estado, cidade, rua, numero virão do front-end quando o usuário inserir o CEP, pois efetuará uma requisição GET
     * para o endpoint público abaixo:
     * brasilapi.com.br/api/cep/v1/{cep}
     */

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
}
