package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "avaliacao")
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @NotNull
    @Column(name = "id_vendedor", nullable = false)
    private int idVendedor;
    @NotNull
    @Column(name = "id_cliente", nullable = false)
    private int idCliente;
    @NotNull
    @Column(name = "nota", nullable = false)
    private float nota;
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
