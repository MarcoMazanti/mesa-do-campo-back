package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Chave PIX salva por um cliente, usada como forma de pagamento (comprador) ou recebimento (vendedor).")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "chave_pix")
public class ChavePix {
    @Schema(description = "Identificador único da chave, gerado pelo banco.", example = "2", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Schema(description = "ID do cliente dono da chave. Precisa ser o próprio usuário autenticado.", example = "1")
    @NotNull
    @Column(name = "id_cliente", nullable = false)
    private int idCliente;

    @Schema(description = "Apelido da chave, definido pelo próprio cliente.", example = "Minha chave principal")
    @NotNull
    @Column(name = "nome", nullable = false)
    private String nome;

    @Schema(description = "Tipo da chave PIX.", example = "EMAIL", allowableValues = {"CPF", "EMAIL", "TELEFONE", "ALEATORIA"})
    @NotNull
    @Column(name = "tipo_chave", nullable = false)
    private String tipoChave;

    @Schema(description = "Valor da chave PIX.", example = "maria@email.com")
    @NotNull
    @Column(name = "chave", nullable = false)
    private String chave;

    @Schema(description = "Se esta é a chave padrão do cliente. Serializado como \"padrao\" no JSON (Jackson remove o prefixo \"is\" do getter isPadrao()).",
            example = "true")
    @Column(name = "is_padrao", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isPadrao;

    public ChavePix(int idCliente, String nome, String tipoChave, String chave, boolean isPadrao) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.tipoChave = tipoChave;
        this.chave = chave;
        this.isPadrao = isPadrao;
    }

    public ChavePix(int idCliente, String nome, String tipoChave, String chave) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.tipoChave = tipoChave;
        this.chave = chave;
        isPadrao = false;
    }
}