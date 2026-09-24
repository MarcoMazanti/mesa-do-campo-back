package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.TipoPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "Conta de vendedor. idVendedor é ao mesmo tempo chave primária e estrangeira para Cliente.id — todo vendedor é um cliente.")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "vendedor")
public class Vendedor {
    @Schema(description = "ID do cliente que se tornou vendedor (mesmo id de Cliente.id).", example = "1")
    @Id
    @NotNull
    @Column(name = "id_vendedor", nullable = false)
    private int idVendedor; // PK e FK apontando para a tabela Usuario

    @Schema(description = "Nota média das avaliações recebidas (0 a 5).", example = "4.5", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "avaliacao")
    private float avaliacao;

    @Schema(description = "Data em que o vendedor se cadastrou.", example = "2026-01-15")
    @Column(name = "data_admissao")
    @PastOrPresent
    private LocalDate dataAdmissao;

    @Schema(description = "Dados de recebimento (chave PIX ou dados bancários em texto livre).", example = "PIX: maria@email.com")
    @Size(max = 255)
    @Column(name = "conta_recebimento")
    private String contaRecebimento; // Aponta para o nome do cartão/chave utilizada para recebimento

    @Schema(description = "Forma preferida de recebimento.", example = "PIX")
    @Column(name = "tipo_pagamento")
    private TipoPagamento tipoPagamento;

    public Vendedor(int idVendedor) {
        this.idVendedor = idVendedor;
        avaliacao = 0;
        dataAdmissao = LocalDate.now();
    }

    public Vendedor(int idVendedor, float avaliacao) {
        this.idVendedor = idVendedor;
        this.avaliacao = avaliacao;
        this.dataAdmissao = LocalDate.now();
    }

    public Vendedor(int idVendedor, LocalDate dataAdmissao) {
        this.idVendedor = idVendedor;
        this.dataAdmissao = dataAdmissao;
        avaliacao = 0;
    }
}
