package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Avaliação que um cliente faz sobre um VENDEDOR (não sobre um produto específico). Um cliente não pode se autoavaliar.")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "avaliacao")
public class Avaliacao {
    @Schema(description = "Identificador único da avaliação, gerado pelo banco.", example = "7", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Schema(description = "ID do vendedor avaliado.", example = "1")
    @NotNull
    @Column(name = "id_vendedor", nullable = false)
    private int idVendedor;
    @Schema(description = "ID do cliente autor da avaliação.", example = "2")
    @NotNull
    @Column(name = "id_cliente", nullable = false)
    private int idCliente;
    @Schema(description = "Nota de 0 a 5.", example = "4.5")
    @NotNull
    @Column(name = "nota", nullable = false)
    private float nota;
    @Schema(description = "Comentário opcional sobre a experiência.", example = "Produtos sempre frescos e entrega rápida!")
    @Column(name = "descricao")
    private String descricao;

    public Avaliacao(int idVendedor, int idCliente, float nota, String descricao) {
        this.idVendedor = idVendedor;
        this.idCliente = idCliente;
        this.nota = nota;
        this.descricao = descricao;
    }

    public Avaliacao(int idVendedor, int idCliente, float nota) {
        this.idVendedor = idVendedor;
        this.idCliente = idCliente;
        this.nota = nota;
    }
}
