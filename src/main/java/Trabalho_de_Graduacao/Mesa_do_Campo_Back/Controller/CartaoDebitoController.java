package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.CartaoDebito;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.External.ReturnModel;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.CartaoDebitoService;
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

@Tag(name = "Cartão de Débito")
@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/cartao-debito")
public class CartaoDebitoController {
    @Autowired
    private CartaoDebitoService cartaoDebitoService;

    @Operation(summary = "Busca um cartão de débito por ID", description = "Só o dono do cartão pode consultá-lo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cartao-debito/unique/6",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 6, "idCliente": 1, "nome": "Débito Banco X", "bandeira": "Mastercard", "ultimosDigitos": 7788, "tokenGateway": "tok_f6e5d4c3b2", "padrao": false }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O cartão não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Cartão de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/cartao-debito/unique/9",
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
                                        "path": "/api/cartao-debito/unique/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi eocontrado um cartão de débito com o ID: 999", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/unique/{id}")
    public ResponseEntity<CartaoDebito> getCartaoDebitoById(@Parameter(description = "ID do cartão.", example = "6") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoDebitoService.getById(id, idUsuarioAuth));
    }

    @Operation(summary = "Lista os cartões de débito do usuário autenticado", description = "Retorna lista vazia se não houver nenhum (não gera erro 404).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso (pode vir vazia).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cartao-debito/auto",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 6, "idCliente": 1, "nome": "Débito Banco X", "bandeira": "Mastercard", "ultimosDigitos": 7788, "tokenGateway": "tok_f6e5d4c3b2", "padrao": false }
                                        ]
                                    }
                                    """)))
    })
    @GetMapping("/auto")
    public ResponseEntity<List<CartaoDebito>> getCartaoDebitoByUsuario(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoDebitoService.getAllCartaoDebitoByIdCliente(idUsuarioAuth));
    }

    @Operation(summary = "Busca o cartão de débito padrão do usuário autenticado", description = "Cartão marcado com padrao = true.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão padrão encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cartao-debito/active",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 6, "idCliente": 1, "nome": "Débito Banco X", "bandeira": "Mastercard", "ultimosDigitos": 7788, "tokenGateway": "tok_f6e5d4c3b2", "padrao": true }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não há cartões cadastrados, ou nenhum deles está marcado como padrão.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "Nenhum cartão", value = """
                                            {
                                                "status": 404,
                                                "path": "/api/cartao-debito/active",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 404, "message": "Não possui nenhum cartão de débito cadastrado para este usuário.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Nenhum padrão definido", value = """
                                            {
                                                "status": 404,
                                                "path": "/api/cartao-debito/active",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 404, "message": "Não possui nenhum cartão de débito ativo para o cliente com o ID: 1", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            }))
    })
    @GetMapping("/active")
    public ResponseEntity<CartaoDebito> getCartaoDebitoActive(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoDebitoService.getCartaoDebitoAtivo(idUsuarioAuth));
    }

    @Operation(summary = "Cadastra um novo cartão de débito", description = "idCliente do corpo precisa ser o usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão criado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cartao-debito/create",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 7, "idCliente": 1, "nome": "Débito Banco Y", "bandeira": "Elo", "ultimosDigitos": 3344, "tokenGateway": "tok_q1w2e3", "padrao": false }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "idCliente do corpo diferente do usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Cartão para terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/cartao-debito/create",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Não se pode criar um cartão para um terceiro.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PostMapping("/create")
    public ResponseEntity<CartaoDebito> createCartaoDebito(@RequestBody CartaoDebito cartaoDebito, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoDebitoService.createCartaoDebito(cartaoDebito, idUsuarioAuth));
    }

    @Operation(summary = "Atualiza um cartão de débito", description = "Atualiza nome, bandeira, últimos dígitos e token de gateway. Só o dono do cartão pode alterá-lo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão atualizado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cartao-debito/update",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 6, "idCliente": 1, "nome": "Débito atualizado", "bandeira": "Mastercard", "ultimosDigitos": 7788, "tokenGateway": "tok_f6e5d4c3b2", "padrao": false }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Cartão de terceiro.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Cartão de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/cartao-debito/update",
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
                                        "path": "/api/cartao-debito/update",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado o cartão solicitado para atualizar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PutMapping("/update")
    public ResponseEntity<CartaoDebito> updateCartaoDebito(@RequestBody CartaoDebito cartaoDebito, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoDebitoService.updateCartaoDebito(cartaoDebito, idUsuarioAuth));
    }

    @Operation(summary = "Define um cartão de débito como padrão", description = "Qualquer outro cartão de débito do cliente que estivesse marcado como padrão é desmarcado antes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão definido como padrão.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cartao-debito/active/7",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 7, "idCliente": 1, "nome": "Débito Banco Y", "bandeira": "Elo", "ultimosDigitos": 3344, "tokenGateway": "tok_q1w2e3", "padrao": true }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe cartão com esse ID para o usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/cartao-debito/active/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado um cartão de débito com o ID: 999para o cliente com o ID: 1 ativar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PatchMapping("/active/{id}")
    public ResponseEntity<CartaoDebito> ativarCartaoDebito(@Parameter(description = "ID do cartão a marcar como padrão.", example = "7") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(cartaoDebitoService.ativarCartaoDebito(id, idUsuarioAuth));
    }

    @Operation(summary = "Remove o cartão de débito como padrão", description = "Só desmarca padrao = false; o cartão continua existindo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão desmarcado como padrão (sem corpo)."),
            @ApiResponse(responseCode = "404", description = "Não existe cartão com esse ID para o usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/cartao-debito/deactivate/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado um cartão de débito com o ID: 999para o cliente com o ID: 1 desativar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<Void> desativarCartaoDebito(@Parameter(description = "ID do cartão a desmarcar.", example = "7") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        cartaoDebitoService.desativarCartaoDebito(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Exclui um cartão de débito", description = "Só o dono do cartão pode excluí-lo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão excluído com sucesso (sem corpo)."),
            @ApiResponse(responseCode = "401", description = "O cartão não pertence ao usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Cartão de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/cartao-debito/9",
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
                                        "path": "/api/cartao-debito/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado um cartão de débito com o ID: 999para o cliente com o ID: 1 para deletar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCartaoDebito(@Parameter(description = "ID do cartão a excluir.", example = "6") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        cartaoDebitoService.deleteCartaoDebito(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
