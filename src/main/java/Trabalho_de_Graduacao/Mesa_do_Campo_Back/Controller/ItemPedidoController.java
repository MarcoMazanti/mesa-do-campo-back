package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.External.ReturnModel;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.ItemPedidoService;
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
import java.util.Map;

@Tag(name = "Item do Pedido")
@RestController
@RequestMapping("/api/item-pedido")
public class ItemPedidoController {
    @Autowired
    private ItemPedidoService itemPedidoService;

    @Operation(summary = "Busca um item por ID", description = "Acesso liberado para o comprador do pedido OU para o vendedor do produto daquele item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/item-pedido/unique/12",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 12, "idPedido": 5, "idProduto": 10, "quantidade": 2, "precoUnit": 8.90, "status": "PENDENTE", "dataCompra": "2026-09-20T14:30:00" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Nem comprador nem vendedor do produto — sem acesso a este item.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sem acesso", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/item-pedido/unique/12",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Você não tem acesso a este item.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe item com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/item-pedido/unique/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Item de pedido não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/unique/{id}")
    public ResponseEntity<ItemPedido> getById(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(itemPedidoService.getById(id, idUsuarioAuth));
    }

    @Operation(summary = "Lista os itens de um pedido", description = "Só o comprador dono do pedido pode listar os itens por aqui.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/item-pedido/pedido/5",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 12, "idPedido": 5, "idProduto": 10, "quantidade": 2, "precoUnit": 8.90, "status": "PENDENTE", "dataCompra": "2026-09-20T14:30:00" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O pedido não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Pedido de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/item-pedido/pedido/9",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Este pedido não pertence ao usuário.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado, ou ele não possui itens.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "Pedido inexistente", value = """
                                            {
                                                "status": 404,
                                                "path": "/api/item-pedido/pedido/999",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 404, "message": "Pedido não encontrado.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Sem itens", value = """
                                            {
                                                "status": 404,
                                                "path": "/api/item-pedido/pedido/6",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 404, "message": "Este pedido não possui itens.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            }))
    })
    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<ItemPedido>> getAllByPedido(@PathVariable("idPedido") int idPedido, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(itemPedidoService.getAllByPedido(idPedido, idUsuarioAuth));
    }

    @Operation(summary = "Lista os itens vendidos por um vendedor",
            description = "Usado em \"Meu Negócio\" (Pedidos Recebidos e relatórios). idVendedor precisa ser o próprio usuário autenticado. Cortado em lotes (headers limit/batch).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/item-pedido/vendedor/1",
                                        "success": true,
                                        "quantity": 1,
                                        "batch": { "quantity": 10, "loteAtual": 1, "loteTotal": 1 },
                                        "itens": [
                                            { "id": 12, "idPedido": 5, "idProduto": 10, "quantidade": 2, "precoUnit": 8.90, "status": "PENDENTE", "dataCompra": "2026-09-20T14:30:00" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Tentativa de consultar itens vendidos por outro vendedor.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Vendedor de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/item-pedido/vendedor/2",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Só é possível consultar os próprios itens vendidos.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Esse vendedor ainda não vendeu nenhum item.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Nenhuma venda", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/item-pedido/vendedor/1",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Nenhum item vendido encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<List<ItemPedido>> getAllByVendedor(@PathVariable("idVendedor") int idVendedor, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(itemPedidoService.getAllByVendedor(idVendedor, idUsuarioAuth));
    }

    @Operation(summary = "Adiciona um item avulso a um pedido existente",
            description = "Só funciona enquanto o pedido estiver AGUARDANDO_PAGAMENTO. O preço é sempre travado no valor atual do produto, e o estoque é descontado. Recalcula automaticamente Pedido.precoTotal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item criado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/item-pedido/create",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 13, "idPedido": 6, "idProduto": 11, "quantidade": 1, "precoUnit": 4.50, "status": "PENDENTE", "dataCompra": null }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Pedido de terceiro, pedido já não está mais aguardando pagamento, ou estoque insuficiente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "Pedido de terceiro", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/item-pedido/create",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Este pedido não pertence ao usuário.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Pedido já em processamento", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/item-pedido/create",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Só é possível adicionar itens a um pedido aguardando pagamento.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Estoque insuficiente", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/item-pedido/create",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Estoque insuficiente para o produto: Alface Crespa", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "404", description = "Pedido ou produto não encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/item-pedido/create",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Pedido não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PostMapping("/create")
    public ResponseEntity<ItemPedido> createItem(@RequestBody ItemPedido item, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(itemPedidoService.createItem(item, idUsuarioAuth));
    }

    @Operation(summary = "Atualiza a quantidade de um item",
            description = "Corpo esperado: { \"quantidade\": 3 }. Só funciona enquanto o pedido estiver AGUARDANDO_PAGAMENTO. Ajusta o estoque pela diferença e recalcula Pedido.precoTotal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/item-pedido/quantidade/12",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 12, "idPedido": 5, "idProduto": 10, "quantidade": 3, "precoUnit": 8.90, "status": "PENDENTE", "dataCompra": "2026-09-20T14:30:00" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Quantidade zero ou negativa, pedido de terceiro, pedido não está mais aguardando pagamento, ou estoque insuficiente para o aumento pedido.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "Quantidade inválida", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/item-pedido/quantidade/12",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "A quantidade deve ser maior que zero.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Estoque insuficiente", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/item-pedido/quantidade/12",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Estoque insuficiente para o produto: Cebola Roxa", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "404", description = "Item, pedido ou produto não encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Item não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/item-pedido/quantidade/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Item de pedido não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PatchMapping("/quantidade/{id}")
    public ResponseEntity<ItemPedido> updateQuantidade(@PathVariable("id") int id, @RequestBody Map<String, Integer> body, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(itemPedidoService.updateQuantidade(id, body.get("quantidade"), idUsuarioAuth));
    }

    @Operation(summary = "Atualiza o status de um item",
            description = "Corpo esperado: { \"status\": \"EM_TRANSITO\" }. Só o vendedor do produto daquele item pode chamar isso. Não é possível alterar um item já CANCELADO ou ENTREGUE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/item-pedido/status/12",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 12, "idPedido": 5, "idProduto": 10, "quantidade": 2, "precoUnit": 8.90, "status": "EM_TRANSITO", "dataCompra": "2026-09-20T14:30:00" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Quem chama não é o vendedor do produto, ou o item já está CANCELADO/ENTREGUE.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "Vendedor de terceiro", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/item-pedido/status/12",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Apenas o vendedor do produto pode atualizar o status do item.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Item já finalizado", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/item-pedido/status/12",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Não é possível alterar um item que já está ENTREGUE.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "404", description = "Item ou produto não encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/item-pedido/status/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Item de pedido não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PatchMapping("/status/{id}")
    public ResponseEntity<ItemPedido> updateStatus(@PathVariable("id") int id, @RequestBody Map<String, String> body, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        StatusItemPedido novoStatus = StatusItemPedido.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(itemPedidoService.atualizarStatus(id, novoStatus, idUsuarioAuth));
    }

    @Operation(summary = "Remove um item de um pedido",
            description = "Só funciona enquanto o pedido estiver AGUARDANDO_PAGAMENTO. Devolve o estoque reservado e recalcula Pedido.precoTotal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removido com sucesso (sem corpo)."),
            @ApiResponse(responseCode = "401", description = "Pedido de terceiro, ou pedido não está mais aguardando pagamento.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Pedido já em processamento", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/item-pedido/12",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Só é possível remover itens de um pedido aguardando pagamento.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Item, pedido ou produto não encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/item-pedido/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Item de pedido não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        itemPedidoService.deleteItem(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
