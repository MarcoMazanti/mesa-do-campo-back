package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/usuario/all")
    public ResponseEntity<List<Pedido>> getAllPedidosByUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.getAllPedidosByUsuario(idUsuarioAuth));
    }

    @GetMapping("/entregue/{id}")
    public ResponseEntity<Pedido> setPedidoEntregue(@PathVariable int id) {
        return ResponseEntity.ok(pedidoService.setPedidoEntregue(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.createPedido(pedido, idUsuarioAuth));
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        pedidoService.cancelarPedido(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
