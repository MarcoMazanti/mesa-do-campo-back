package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.CartaoCredito;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.CartaoCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartao-credito")
public class CartaoCreditoController {
    @Autowired
    private CartaoCreditoService cartaoCreditoService;

    @GetMapping("/unique/{id}")
    public ResponseEntity<CartaoCredito> getCartaoCreditoById(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoCreditoService.getById(id, idUsuarioAuth));
    }

    @GetMapping("/auto")
    public ResponseEntity<List<CartaoCredito>> getCartaoCreditoByUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoCreditoService.getAllCartaoCreditoByIdCliente(idUsuarioAuth));
    }

    @GetMapping("/active")
    public ResponseEntity<CartaoCredito> getCartaoCreditoActive(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoCreditoService.getCartaoCreditoAtivo(idUsuarioAuth));
    }

    @PostMapping("/create")
    public ResponseEntity<CartaoCredito> createCartaoCredito(@RequestBody CartaoCredito cartaoCredito, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoCreditoService.createCartaoCredito(cartaoCredito, idUsuarioAuth));
    }

    @PutMapping("/update")
    public ResponseEntity<CartaoCredito> updateCartaoCredito(@RequestBody CartaoCredito cartaoCredito, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoCreditoService.updateCartaoCredito(cartaoCredito, idUsuarioAuth));
    }

    @PatchMapping("/active/{id}")
    public ResponseEntity<CartaoCredito> ativarCartaoCredito(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoCreditoService.ativarCartaoCredito(id, idUsuarioAuth));
    }

    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<Void> desativarCartaoCredito(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        cartaoCreditoService.desativarCartaoCredito(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCartaoCredito(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        cartaoCreditoService.deleteCartaoCredito(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
