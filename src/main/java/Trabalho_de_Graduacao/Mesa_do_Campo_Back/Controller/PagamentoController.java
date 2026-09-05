package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.TipoPagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {
    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping("/id/{id}")
    public ResponseEntity<Pagamento> findById(@PathVariable int id,
                                              @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pagamentoService.findById(id, idUsuarioAuth));
    }

    @GetMapping("/usuario/all")
    public ResponseEntity<List<Pagamento>> findAllByUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pagamentoService.findAllByUsuario(idUsuarioAuth));
    }

    @PostMapping("/create/{idPedido}")
    public ResponseEntity<Pagamento> create(@PathVariable int idPedido,
                                            @RequestParam("metodo_pagamento") TipoPagamento metodoPagamento,
                                            @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pagamentoService.criarPagamento(idPedido, metodoPagamento, idUsuarioAuth));
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<Pagamento> updateStatus(@PathVariable int id,
                                                  @RequestParam("status") StatusPagamento novoStatus,
                                                  @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pagamentoService.atualizarStatus(id, novoStatus, idUsuarioAuth));
    }
}
