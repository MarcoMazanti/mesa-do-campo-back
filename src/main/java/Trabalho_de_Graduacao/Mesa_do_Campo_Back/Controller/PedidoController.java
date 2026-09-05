package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.CheckoutRequestDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.PedidoDetalhadoDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPedido;
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

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getById(@PathVariable int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.getById(id, idUsuarioAuth));
    }

    // Cabeçalho do pedido + seus itens + o pagamento, tudo em uma só resposta.
    @GetMapping("/detalhe/{id}")
    public ResponseEntity<PedidoDetalhadoDTO> getDetalhe(@PathVariable int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.getDetalhe(id, idUsuarioAuth));
    }

    @GetMapping("/entregue/{id}")
    public ResponseEntity<Pedido> setPedidoEntregue(@PathVariable int id) {
        return ResponseEntity.ok(pedidoService.setPedidoEntregue(id));
    }

    // Corpo esperado: { "status": "ENVIADO" } — ENTREGUE/CANCELADO usam seus próprios endpoints.
    @PatchMapping("/status/{id}")
    public ResponseEntity<Pedido> updateStatus(@PathVariable int id, @RequestParam StatusPedido novoStatus, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, novoStatus, idUsuarioAuth));
    }

    // Criação apenas do "cabeçalho" do pedido — mantido por compatibilidade. Prefira /checkout.
    @PostMapping("/create")
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.createPedido(pedido, idUsuarioAuth));
    }

    /*
     * Finaliza a compra de uma vez só: recebe os itens do carrinho e a forma
     * de pagamento, e cria Pedido + ItemPedido (de cada item) + Pagamento de
     * forma atômica, validando estoque e calculando o total a partir do
     * preço atual de cada produto.
     */
    @PostMapping("/checkout")
    public ResponseEntity<PedidoDetalhadoDTO> checkout(@RequestBody CheckoutRequestDTO checkout, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.checkout(checkout, idUsuarioAuth));
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        pedidoService.cancelarPedido(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
