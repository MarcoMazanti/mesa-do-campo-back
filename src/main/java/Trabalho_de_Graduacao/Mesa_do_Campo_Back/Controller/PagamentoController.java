package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.TipoPagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External.ReturnModel;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.PagamentoService;
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

@Tag(name = "Pagamento")
@SecurityRequirement(name = "basicAuth")
@RestController
@HybridEncrypted
@RequestMapping("/api/pagamentos")
public class PagamentoController {
    @Autowired
    private PagamentoService pagamentoService;

    @Operation(summary = "Busca um pagamento por ID", description = "Só o comprador dono do pedido pago pode consultar o pagamento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/pagamentos/id/5",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 5, "idPedido": 5, "metodoPagamento": "PIX", "status": "APROVADO", "dataPagamento": "2026-09-20T14:31:00", "valorPago": 45.90 }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O pagamento é de um pedido que não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Pagamento de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/pagamentos/id/9",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Não é possível acessar esse pagamento.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe pagamento com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/pagamentos/id/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Pagamento não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<Pagamento> findById(@Parameter(description = "ID do pagamento.", example = "5") @PathVariable int id,
                                              @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pagamentoService.findById(id, idUsuarioAuth));
    }

    @Operation(summary = "Lista os pagamentos do usuário autenticado", description = "Cortado em lotes (headers limit/batch).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/pagamentos/usuario/all",
                                        "success": true,
                                        "quantity": 1,
                                        "batch": { "quantity": 10, "loteAtual": 1, "loteTotal": 1 },
                                        "itens": [
                                            { "id": 5, "idPedido": 5, "metodoPagamento": "PIX", "status": "APROVADO", "dataPagamento": "2026-09-20T14:31:00", "valorPago": 45.90 }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "O usuário autenticado não tem nenhum pagamento.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Nenhum pagamento", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/pagamentos/usuario/all",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Nenhum pagamento encontrado para o usuário.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/usuario/all")
    public ResponseEntity<List<Pagamento>> findAllByUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pagamentoService.findAllByUsuario(idUsuarioAuth));
    }

    @Operation(summary = "Registra o pagamento de um pedido",
            description = "metodo_pagamento é enviado como QUERY PARAM (não no corpo). Simulado: como não há gateway real, o pagamento já é criado como APROVADO e o pedido avança para PROCESSANDO. Normalmente não é preciso chamar isso manualmente — o checkout do Pedido já cria pedido + itens + pagamento numa vez só.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento registrado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/pagamentos/create/5?metodo_pagamento=PIX",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 5, "idPedido": 5, "metodoPagamento": "PIX", "status": "APROVADO", "dataPagamento": "2026-09-20T14:31:00", "valorPago": 45.90 }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Pedido de terceiro, pedido cancelado, ou pedido que já tem pagamento.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "Pedido de terceiro", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/pagamentos/create/9",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Este pedido não pertence ao usuário.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Pedido cancelado", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/pagamentos/create/5",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Não é possível pagar um pedido cancelado.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Já pago", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/pagamentos/create/5",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Este pedido já possui um pagamento registrado.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "404", description = "Não existe pedido com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/pagamentos/create/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Pedido não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PostMapping("/create/{idPedido}")
    public ResponseEntity<Pagamento> create(@Parameter(description = "ID do pedido a pagar.", example = "5") @PathVariable int idPedido,
                                            @Parameter(description = "Forma de pagamento.", example = "PIX") @RequestParam("metodo_pagamento") TipoPagamento metodoPagamento,
                                            @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pagamentoService.criarPagamento(idPedido, metodoPagamento, idUsuarioAuth));
    }

    @Operation(summary = "Atualiza manualmente o status de um pagamento",
            description = "status é enviado como QUERY PARAM (não no corpo). Equivale a um webhook de gateway, já que não existe um real. O status do Pedido é ajustado em cascata (APROVADO→PROCESSANDO, RECUSADO→AGUARDANDO_PAGAMENTO, CANCELADO/ESTORNADO→CANCELADO), desde que o pedido ainda não esteja ENTREGUE/CANCELADO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status do pagamento atualizado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/pagamentos/status/5?status=APROVADO",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 5, "idPedido": 5, "metodoPagamento": "PIX", "status": "APROVADO", "dataPagamento": "2026-09-20T14:31:00", "valorPago": 45.90 }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O pagamento é de um pedido que não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Pagamento de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/pagamentos/status/9",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Não é possível alterar esse pagamento.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe pagamento (ou o pedido associado) com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/pagamentos/status/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Pagamento não encontrado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PatchMapping("/status/{id}")
    public ResponseEntity<Pagamento> updateStatus(@Parameter(description = "ID do pagamento.", example = "5") @PathVariable int id,
                                                  @Parameter(description = "Novo status.", example = "APROVADO") @RequestParam("status") StatusPagamento novoStatus,
                                                  @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(pagamentoService.atualizarStatus(id, novoStatus, idUsuarioAuth));
    }
}
