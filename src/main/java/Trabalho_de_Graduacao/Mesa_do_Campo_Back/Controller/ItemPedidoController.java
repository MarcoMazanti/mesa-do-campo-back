package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.ItemPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/item-pedido")
public class ItemPedidoController {
    @Autowired
    private ItemPedidoService itemPedidoService;

    @GetMapping("/unique/{id}")
    public ResponseEntity<ItemPedido> getById(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(itemPedidoService.getById(id, idUsuarioAuth));
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<ItemPedido>> getAllByPedido(@PathVariable("idPedido") int idPedido, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(itemPedidoService.getAllByPedido(idPedido, idUsuarioAuth));
    }

    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<List<ItemPedido>> getAllByVendedor(@PathVariable("idVendedor") int idVendedor, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(itemPedidoService.getAllByVendedor(idVendedor, idUsuarioAuth));
    }

    @PostMapping("/create")
    public ResponseEntity<ItemPedido> createItem(@RequestBody ItemPedido item, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(itemPedidoService.createItem(item, idUsuarioAuth));
    }

    @PatchMapping("/quantidade/{id}")
    public ResponseEntity<ItemPedido> updateQuantidade(@PathVariable("id") int id, @RequestBody Map<String, Integer> body, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(itemPedidoService.updateQuantidade(id, body.get("quantidade"), idUsuarioAuth));
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<ItemPedido> updateStatus(@PathVariable("id") int id, @RequestBody Map<String, String> body, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        StatusItemPedido novoStatus = StatusItemPedido.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(itemPedidoService.atualizarStatus(id, novoStatus, idUsuarioAuth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        itemPedidoService.deleteItem(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
