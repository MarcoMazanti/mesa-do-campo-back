package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.TipoPagamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "vendedor")
public class Vendedor {
    @Id
    @NotNull
    @Column(name = "id_vendedor", nullable = false)
    private int id_vendedor; // PK e FK apontando para a tabela Usuario

    @Column(name = "avaliacao")
    private float avaliacao;

    @Column(name = "data_admissao")
    @PastOrPresent
    private LocalDate dataAdmissao;

    @Size(max = 255)
    @Column(name = "conta_recebimento")
    private String contaRecebimento; // Aponta para o nome do cartão/chave utilizada para recebimento

    @Column(name = "tipo_pagamento")
    private TipoPagamento tipoPagamento;

    public Vendedor(int id_vendedor) {
        this.id_vendedor = id_vendedor;
        avaliacao = 0;
        dataAdmissao = LocalDate.now();
    }

    public Vendedor(int id_vendedor, float avaliacao) {
        this.id_vendedor = id_vendedor;
        this.avaliacao = avaliacao;
        this.dataAdmissao = LocalDate.now();
    }

    public Vendedor(int id_vendedor, LocalDate dataAdmissao) {
        this.id_vendedor = id_vendedor;
        this.dataAdmissao = dataAdmissao;
        avaliacao = 0;
    }
}
