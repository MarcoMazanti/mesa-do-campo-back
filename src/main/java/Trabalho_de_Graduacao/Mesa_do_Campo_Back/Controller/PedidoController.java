package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.CheckoutRequestDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.PedidoDetalhadoDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External.ReturnModel;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.PedidoService;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.HybridEncrypted;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pedido")
@SecurityRequirement(name = "basicAuth")
@RestController
@HybridEncrypted
@RequestMapping("/api/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @Operation(summary = "Lista os pedidos do usuário autenticado", description = "Só retorna os pedidos feitos pelo próprio usuário (como comprador). Cortado em lotes (headers limit/batch).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/pedidos/usuario/all",
                                        "success": true,
                                        "quantity": 1,
                                        "batch": { "quantity": 10, "loteAtual": 1, "loteTotal": 1 },
                                        "itens": [
                                            { "id": 5, "idCliente": 1, "precoTotal": 45.90, "dataCompra": "2026-09-20T14:30:00", "status": "PROCESSANDO" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "O usuário autenticado ainda não fez nenhum pedido.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sem pedidos", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/pedidos/usuario/all",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não existem pedidos para o usuário.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/usuario/all")
    public ResponseEntity<List<Pedido>> getAllPedidosByUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.getAllPedidosByUsuario(idUsuarioAuth));
    }

    @Operation(summary = "Busca um pedido por ID", description = "Só o comprador dono do pedido pode consultá-lo — nem mesmo um vendedor com item naquele pedido tem acesso por aqui.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/pedidos/5",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 5, "idCliente": 1, "precoTotal": 45.90, "dataCompra": "2026-09-20T14:30:00", "status": "PROCESSANDO" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O pedido não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Pedido de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/pedidos/9",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Este pedido não pertence ao usuário.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe pedido com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/pedidos/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Pedido não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getById(@Parameter(description = "ID do pedido.", example = "5") @PathVariable int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.getById(id, idUsuarioAuth));
    }

    @Operation(summary = "Detalhe completo de um pedido",
            description = "Cabeçalho do pedido + todos os itens + o pagamento, em uma resposta só. \"pagamento\" pode vir nulo se o pedido ainda não tiver nenhum pagamento registrado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhe retornado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/pedidos/detalhe/5",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            {
                                                "pedido": { "id": 5, "idCliente": 1, "precoTotal": 45.90, "dataCompra": "2026-09-20T14:30:00", "status": "PROCESSANDO" },
                                                "itens": [
                                                    { "id": 12, "idPedido": 5, "idProduto": 10, "quantidade": 2, "precoUnit": 8.90, "status": "PENDENTE", "dataCompra": "2026-09-20T14:30:00" }
                                                ],
                                                "pagamento": { "id": 5, "idPedido": 5, "metodoPagamento": "PIX", "status": "APROVADO", "dataPagamento": "2026-09-20T14:30:00", "valorPago": 45.90 }
                                            }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O pedido não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Pedido de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/pedidos/detalhe/9",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Este pedido não pertence ao usuário.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe pedido com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/pedidos/detalhe/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Pedido não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/detalhe/{id}")
    public ResponseEntity<PedidoDetalhadoDTO> getDetalhe(@Parameter(description = "ID do pedido.", example = "5") @PathVariable int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.getDetalhe(id, idUsuarioAuth));
    }

    @Operation(summary = "Marca o pedido como ENTREGUE",
            description = "Apesar do verbo HTTP ser GET, este endpoint executa uma mutação (muda o status do pedido). Não é permitido confirmar a entrega de um pedido já cancelado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido marcado como entregue.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/pedidos/entregue/5",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 5, "idCliente": 1, "precoTotal": 45.90, "dataCompra": "2026-09-20T14:30:00", "status": "ENTREGUE" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O pedido já está cancelado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Já cancelado", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/pedidos/entregue/5",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Pedido já cancelado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe pedido com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/pedidos/entregue/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Pedido não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/entregue/{id}")
    public ResponseEntity<Pedido> setPedidoEntregue(@Parameter(description = "ID do pedido.", example = "5") @PathVariable int id) {
        return ResponseEntity.ok(pedidoService.setPedidoEntregue(id));
    }

    @Operation(summary = "Atualiza o status do pedido",
            description = "novoStatus é enviado como QUERY PARAM (não no corpo). Só aceita AGUARDANDO_PAGAMENTO, PROCESSANDO ou ENVIADO — para ENTREGUE use /entregue/{id}, e para CANCELADO use /cancel/{id}.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/pedidos/status/5",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 5, "idCliente": 1, "precoTotal": 45.90, "dataCompra": "2026-09-20T14:30:00", "status": "ENVIADO" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "novoStatus é ENTREGUE/CANCELADO, o pedido não pertence ao usuário, ou o pedido já está finalizado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "Status não permitido aqui", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/pedidos/status/5",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Use os endpoints /pedidos/entregue/{id} ou /pedidos/cancel/{id} para este status.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Pedido de terceiro", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/pedidos/status/9",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Este pedido não pertence ao usuário.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Pedido já finalizado", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/pedidos/status/5",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Não é possível alterar um pedido ENTREGUE.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "404", description = "Não existe pedido com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/pedidos/status/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Pedido não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PatchMapping("/status/{id}")
    public ResponseEntity<Pedido> updateStatus(@Parameter(description = "ID do pedido.", example = "5") @PathVariable int id,
                                               @Parameter(description = "Novo status desejado.", example = "ENVIADO") @RequestParam StatusPedido novoStatus,
                                               @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, novoStatus, idUsuarioAuth));
    }

    @Operation(summary = "Cria apenas o \"cabeçalho\" de um pedido",
            description = "Mantido por compatibilidade — não cria itens nem pagamento. idCliente do corpo precisa ser igual ao usuário autenticado. Prefira POST /checkout para uma compra completa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido criado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/pedidos/create",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 6, "idCliente": 1, "precoTotal": 0.00, "dataCompra": "2026-09-20T14:30:00", "status": "AGUARDANDO_PAGAMENTO" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O idCliente do corpo não é o usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Pedido para terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/pedidos/create",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Pedido não pertence ao usuário.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PostMapping("/create")
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.createPedido(pedido, idUsuarioAuth));
    }

    @Operation(summary = "Finaliza a compra (checkout completo)",
            description = "Endpoint recomendado para comprar: recebe os itens do carrinho e a forma de pagamento, e cria de forma ATÔMICA o Pedido, um ItemPedido para cada item (validando e descontando estoque) e o Pagamento (simulado, já criado como APROVADO). O preço usado é sempre o preço ATUAL do produto no banco — nunca um valor enviado pelo front.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra finalizada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/pedidos/checkout",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            {
                                                "pedido": { "id": 5, "idCliente": 1, "precoTotal": 17.80, "dataCompra": "2026-09-20T14:30:00", "status": "PROCESSANDO" },
                                                "itens": [
                                                    { "id": 12, "idPedido": 5, "idProduto": 10, "quantidade": 2, "precoUnit": 8.90, "status": "PENDENTE", "dataCompra": "2026-09-20T14:30:00" }
                                                ],
                                                "pagamento": { "id": 5, "idPedido": 5, "metodoPagamento": "PIX", "status": "APROVADO", "dataPagamento": "2026-09-20T14:30:00", "valorPago": 17.80 }
                                            }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Carrinho vazio ou estoque insuficiente para algum item.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "Carrinho vazio", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/pedidos/checkout",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "O pedido precisa ter ao menos um item.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Estoque insuficiente", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/pedidos/checkout",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Estoque insuficiente para o produto: Cebola Roxa", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "404", description = "Algum produto do carrinho não existe.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Produto inexistente", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/pedidos/checkout",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Produto não encontrado: 999", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PostMapping("/checkout")
    public ResponseEntity<PedidoDetalhadoDTO> checkout(@RequestBody CheckoutRequestDTO checkout, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pedidoService.checkout(checkout, idUsuarioAuth));
    }

    @Operation(summary = "Cancela um pedido",
            description = "Cancela todos os itens (devolvendo o estoque de cada produto), estorna/cancela o pagamento se houver, e marca o pedido como CANCELADO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso (sem corpo)."),
            @ApiResponse(responseCode = "401", description = "O pedido não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Pedido de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/pedidos/cancel/9",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Pedido não pertence ao usuário.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe pedido com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/pedidos/cancel/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Pedido não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> deletePedido(@Parameter(description = "ID do pedido a cancelar.", example = "5") @PathVariable int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        pedidoService.cancelarPedido(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
