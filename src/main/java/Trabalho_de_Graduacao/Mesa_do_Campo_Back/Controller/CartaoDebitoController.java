package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.CartaoDebito;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.CartaoDebitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartao-debito")
public class CartaoDebitoController {
    @Autowired
    private CartaoDebitoService cartaoDebitoService;

    @GetMapping("/unique/{id}")
    public ResponseEntity<CartaoDebito> getCartaoDebitoById(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoDebitoService.getById(id, idUsuarioAuth));
    }

    @GetMapping("/auto")
    public ResponseEntity<List<CartaoDebito>> getCartaoDebitoByUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoDebitoService.getAllCartaoDebitoByIdCliente(idUsuarioAuth));
    }

    @GetMapping("/active")
    public ResponseEntity<CartaoDebito> getCartaoDebitoActive(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoDebitoService.getCartaoDebitoAtivo(idUsuarioAuth));
    }

    @PostMapping("/create")
    public ResponseEntity<CartaoDebito> createCartaoDebito(@RequestBody CartaoDebito cartaoDebito, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoDebitoService.createCartaoDebito(cartaoDebito, idUsuarioAuth));
    }

    @PutMapping("/update")
    public ResponseEntity<CartaoDebito> updateCartaoDebito(@RequestBody CartaoDebito cartaoDebito, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoDebitoService.updateCartaoDebito(cartaoDebito, idUsuarioAuth));
    }

    @PatchMapping("/active/{id}")
    public ResponseEntity<CartaoDebito> ativarCartaoDebito(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoDebitoService.ativarCartaoDebito(id, idUsuarioAuth));
    }

    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<Void> desativarCartaoDebito(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        cartaoDebitoService.desativarCartaoDebito(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCartaoDebito(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        cartaoDebitoService.deleteCartaoDebito(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
