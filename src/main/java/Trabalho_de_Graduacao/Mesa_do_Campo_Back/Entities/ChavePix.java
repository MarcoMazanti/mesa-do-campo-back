package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "chave_pix")
public class ChavePix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Column(name = "id_cliente", nullable = false)
    private int idCliente;

    @NotNull
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotNull
    @Column(name = "tipo_chave", nullable = false)
    private String tipoChave;

    @NotNull
    @Column(name = "chave", nullable = false)
    private String chave;

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