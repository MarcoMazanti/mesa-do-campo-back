package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.CategoriaProduto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External.ReturnModel;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Produto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.ProdutoService;
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

@Tag(name = "Produto")
@HybridEncrypted
@RestController
@RequestMapping("/api/produto")
public class ProdutoController {
    @Autowired
    private ProdutoService produtoService;

    @Operation(summary = "Busca um produto por ID", description = "Catálogo público — não exige autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/produto/unique/10",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 10, "idVendedor": 1, "nome": "Cebola Roxa", "preco": 8.90, "quantidade": 50, "categoria": "VERDURAS", "descricao": "Cebola roxa fresca, colhida na semana." }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe produto com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/produto/unique/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhum produto com o ID: 999", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/unique/{id}")
    public ResponseEntity<Produto> getProdutoById(@Parameter(description = "ID do produto.", example = "10") @PathVariable("id") int id) {
        return ResponseEntity.ok(produtoService.getById(id));
    }

    @Operation(summary = "Lista os produtos de um vendedor", description = "Catálogo público — não exige autenticação. Usado em \"Meu Negócio\" e na página de um vendedor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/produto/vendedor/1",
                                        "success": true,
                                        "quantity": 1,
                                        "batch": { "quantity": 10, "loteAtual": 1, "loteTotal": 1 },
                                        "itens": [
                                            { "id": 10, "idVendedor": 1, "nome": "Cebola Roxa", "preco": 8.90, "quantidade": 50, "categoria": "VERDURAS", "descricao": "Cebola roxa fresca, colhida na semana." }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Esse vendedor não tem nenhum produto cadastrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Nenhum produto", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/produto/vendedor/5",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhum produto para este vendedor.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<List<Produto>> getProdutoByVendedor(@Parameter(description = "ID do vendedor.", example = "1") @PathVariable("idVendedor") int idVendedor) {
        return ResponseEntity.ok(produtoService.getAllProdutosByIdVendedor(idVendedor));
    }

    @Operation(summary = "Lista todo o catálogo", description = "Catálogo público — não exige autenticação. Cortado em lotes (headers limit/batch).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/produto/all",
                                        "success": true,
                                        "quantity": 24,
                                        "batch": { "quantity": 12, "loteAtual": 1, "loteTotal": 2 },
                                        "itens": [
                                            { "id": 10, "idVendedor": 1, "nome": "Cebola Roxa", "preco": 8.90, "quantidade": 50, "categoria": "VERDURAS", "descricao": "Cebola roxa fresca, colhida na semana." }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não há nenhum produto cadastrado no sistema.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Catálogo vazio", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/produto/all",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não possui produtos cadastrados.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<Produto>> getAllProduto() {
        return ResponseEntity.ok(produtoService.getAllProdutos());
    }

    @Operation(summary = "Lista os produtos de uma categoria",
            description = "Catálogo público — não exige autenticação. O valor da categoria (path) é convertido para maiúsculas antes de ser comparado ao enum CategoriaProduto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/produto/categoria/verduras",
                                        "success": true,
                                        "quantity": 3,
                                        "batch": { "quantity": 10, "loteAtual": 1, "loteTotal": 1 },
                                        "itens": [
                                            { "id": 10, "idVendedor": 1, "nome": "Cebola Roxa", "preco": 8.90, "quantidade": 50, "categoria": "VERDURAS", "descricao": "Cebola roxa fresca, colhida na semana." }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não há produtos cadastrados nessa categoria.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Categoria vazia", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/produto/categoria/ovos",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não possui produtos cadastrados.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "O valor enviado não corresponde a nenhuma categoria válida (VERDURAS, LEGUMES, FRUTAS, LATICINIOS, GRAOS, CEREAIS, OVOS, OUTROS).",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Categoria inválida", value = """
                                    { "status": 500, "message": "Erro interno: No enum constant ...CategoriaProduto.PEIXES" }
                                    """)))
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Produto>> getProdutoByCategoria(@Parameter(description = "Nome da categoria (case-insensitive).", example = "verduras") @PathVariable("categoria") String categoria) {
        return ResponseEntity.ok(produtoService.getAllProdutosByCategoria(CategoriaProduto.valueOf(categoria.toUpperCase())));
    }

    @Operation(summary = "Cadastra um novo produto",
            description = "Apenas o próprio vendedor (Produto.idVendedor precisa ser igual ao usuário autenticado, e já ter uma conta de vendedor) pode cadastrar produtos.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto criado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/produto/create",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 11, "idVendedor": 1, "nome": "Alface Crespa", "preco": 4.50, "quantidade": 30, "categoria": "VERDURAS", "descricao": "Alface orgânica" }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O vendedor informado não existe, ou não é o usuário autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "Vendedor inexistente", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/produto/create",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Não possui nenhum vendedor com o ID: 99 cadastrado.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Vendedor de terceiro", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/produto/create",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Apenas o vendedor pode cadastrar produtos.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            }))
    })
    @PostMapping("/create")
    public ResponseEntity<Produto> createProduto(@RequestBody Produto produto, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(produtoService.createProduto(produto, idUsuarioAuth));
    }

    @Operation(summary = "Atualiza um produto",
            description = "Atualiza nome, preço, descrição, quantidade e categoria. Só o vendedor dono do produto pode alterá-lo.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/produto/update",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 10, "idVendedor": 1, "nome": "Cebola Roxa", "preco": 9.50, "quantidade": 40, "categoria": "VERDURAS", "descricao": "Cebola roxa fresca, colhida na semana." }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O produto não pertence ao vendedor autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Produto de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/produto/update",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Apenas o vendedor pode alterar os produtos.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe produto com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/produto/update",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhum produto com o ID: 999para atualizar.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PutMapping("/update")
    public ResponseEntity<Produto> updateProduto(@RequestBody Produto produto, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(produtoService.updateProduto(produto, idUsuarioAuth));
    }

    @Operation(summary = "Exclui um produto", description = "Só o vendedor dono do produto pode excluí-lo.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto excluído com sucesso (sem corpo)."),
            @ApiResponse(responseCode = "401", description = "O produto não pertence ao vendedor autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Produto de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/produto/10",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Apenas o vendedor pode excluir os produtos.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe produto com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/produto/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhum produto com o ID: 999para excluir.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@Parameter(description = "ID do produto a excluir.", example = "10") @PathVariable("id") int id, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        produtoService.deleteProduto(id, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
