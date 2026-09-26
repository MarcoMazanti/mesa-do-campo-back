package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.CartaoCredito;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.External.ReturnModel;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.CartaoCreditoService;
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

@Tag(name = "Cartão de Crédito")
@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/cartao-credito")
public class CartaoCreditoController {
    @Autowired
    private CartaoCreditoService cartaoCreditoService;

    @Operation(summary = "Busca um cartão de crédito por ID", description = "Só o dono do cartão pode consultá-lo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cartao-credito/unique/4",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 4, "idCliente": 1, "nome": "Cartão principal", "bandeira": "Visa", "ultimosDigitos": 4321, "tokenGateway": "tok_a1b2c3d4e5", "padrao": true }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O cartão não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Cartão de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/cartao-credito/unique/9",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Apenas o cliente pode visualizar seus dados.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe cartão com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/cartao-credito/unique/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi eocontrado um cartão de crédito com o ID: 999", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/unique/{id}")
    public ResponseEntity<CartaoCredito> getCartaoCreditoById(@Parameter(description = "ID do cartão.", example = "4") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoCreditoService.getById(id, idUsuarioAuth));
    }

    @Operation(summary = "Lista os cartões de crédito do usuário autenticado", description = "Retorna lista vazia se não houver nenhum (não gera erro 404).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso (pode vir vazia).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cartao-credito/auto",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 4, "idCliente": 1, "nome": "Cartão principal", "bandeira": "Visa", "ultimosDigitos": 4321, "tokenGateway": "tok_a1b2c3d4e5", "padrao": true }
                                        ]
                                    }
                                    """)))
    })
    @GetMapping("/auto")
    public ResponseEntity<List<CartaoCredito>> getCartaoCreditoByUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoCreditoService.getAllCartaoCreditoByIdCliente(idUsuarioAuth));
    }

    @Operation(summary = "Busca o cartão de crédito padrão do usuário autenticado", description = "Cartão marcado com padrao = true.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão padrão encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cartao-credito/active",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 4, "idCliente": 1, "nome": "Cartão principal", "bandeira": "Visa", "ultimosDigitos": 4321, "tokenGateway": "tok_a1b2c3d4e5", "padrao": true }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não há cartões cadastrados, ou nenhum deles está marcado como padrão.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "Nenhum cartão", value = """
                                            {
                                                "status": 404,
                                                "path": "/api/cartao-credito/active",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 404, "message": "Não possui nenhum cartão de crédito cadastrado para este usuário.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Nenhum padrão definido", value = """
                                            {
                                                "status": 404,
                                                "path": "/api/cartao-credito/active",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 404, "message": "Não possui nenhum cartão de crédito ativo para o cliente com o ID: 1", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            }))
    })
    @GetMapping("/active")
    public ResponseEntity<CartaoCredito> getCartaoCreditoActive(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoCreditoService.getCartaoCreditoAtivo(idUsuarioAuth));
    }

    @Operation(summary = "Cadastra um novo cartão de crédito", description = "idCliente do corpo precisa ser o usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão criado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cartao-credito/create",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 5, "idCliente": 1, "nome": "Cartão reserva", "bandeira": "Mastercard", "ultimosDigitos": 1122, "tokenGateway": "tok_z9y8x7", "padrao": false }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "idCliente do corpo diferente do usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Cartão para terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/cartao-credito/create",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Não se pode criar um cartão para um terceiro.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PostMapping("/create")
    public ResponseEntity<CartaoCredito> createCartaoCredito(@RequestBody CartaoCredito cartaoCredito, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoCreditoService.createCartaoCredito(cartaoCredito, idUsuarioAuth));
    }

    @Operation(summary = "Atualiza um cartão de crédito", description = "Atualiza nome, bandeira, últimos dígitos e token de gateway. Só o dono do cartão pode alterá-lo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão atualizado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cartao-credito/update",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 4, "idCliente": 1, "nome": "Cartão do trabalho", "bandeira": "Visa", "ultimosDigitos": 4321, "tokenGateway": "tok_a1b2c3d4e5", "padrao": true }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Cartão de terceiro.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Cartão de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/cartao-credito/update",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Não se pode criar um cartão para um terceiro.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe cartão com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/cartao-credito/update",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado o cartão solicitado para atualizar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PutMapping("/update")
    public ResponseEntity<CartaoCredito> updateCartaoCredito(@RequestBody CartaoCredito cartaoCredito, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoCreditoService.updateCartaoCredito(cartaoCredito, idUsuarioAuth));
    }

    @Operation(summary = "Define um cartão de crédito como padrão", description = "Qualquer outro cartão de crédito do cliente que estivesse marcado como padrão é desmarcado antes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão definido como padrão.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cartao-credito/active/5",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 5, "idCliente": 1, "nome": "Cartão reserva", "bandeira": "Mastercard", "ultimosDigitos": 1122, "tokenGateway": "tok_z9y8x7", "padrao": true }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe cartão com esse ID para o usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/cartao-credito/active/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado um cartão de crédito com o ID: 999para o cliente com o ID: 1 ativar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PatchMapping("/active/{id}")
    public ResponseEntity<CartaoCredito> ativarCartaoCredito(@Parameter(description = "ID do cartão a marcar como padrão.", example = "5") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoCreditoService.ativarCartaoCredito(id, idUsuarioAuth));
    }

    @Operation(summary = "Remove o cartão de crédito como padrão", description = "Só desmarca padrao = false; o cartão continua existindo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão desmarcado como padrão (sem corpo)."),
            @ApiResponse(responseCode = "404", description = "Não existe cartão com esse ID para o usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/cartao-credito/deactivate/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado um cartão de crédito com o ID: 999para o cliente com o ID: 1 desativar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<Void> desativarCartaoCredito(@Parameter(description = "ID do cartão a desmarcar.", example = "5") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        cartaoCreditoService.desativarCartaoCredito(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Exclui um cartão de crédito", description = "Só o dono do cartão pode excluí-lo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão excluído com sucesso (sem corpo)."),
            @ApiResponse(responseCode = "401", description = "O cartão não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Cartão de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/cartao-credito/9",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Não se pode deletar um cartão de terceiro", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe cartão com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/cartao-credito/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado um cartão de crédito com o ID: 999para o cliente com o ID: 1 para deletar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCartaoCredito(@Parameter(description = "ID do cartão a excluir.", example = "5") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        cartaoCreditoService.deleteCartaoCredito(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
