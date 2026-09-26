package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.ClienteDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.LoginDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External.ReturnModel;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RequisicaoIncompletaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.ClienteService;
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
import java.util.Map;

@Tag(name = "Cliente")
@HybridEncrypted
@RestController
@RequestMapping("/api/cliente")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "Dados do cliente autenticado",
            description = "Retorna os dados (sem a senha) do cliente dono das credenciais enviadas no Basic Auth.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cliente/auto",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 1, "nome": "Maria da Silva", "cpfOrCnpj": "12345678900", "email": "maria@email.com", "telefone": "14999998888", "idEnderecoEntrega": 3 }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Credenciais ausentes ou inválidas no Basic Auth (resposta em texto puro, não segue o envelope ReturnModel).",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(name = "Não autenticado", value = "Modelo de autenticação incorreta, utilize Basic Auth.")))
    })
    @GetMapping("/auto")
    public ResponseEntity<ClienteDTO> getCliente(@RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(clienteService.getById(idUsuarioAuth));
    }

    @Operation(summary = "Busca um cliente por ID", description = "Retorna os dados públicos (sem a senha) de qualquer cliente cadastrado. Endpoint público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cliente/2",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 2, "nome": "João Pedro", "cpfOrCnpj": "98765432100", "email": "joao@email.com", "telefone": "14988887777", "idEnderecoEntrega": null }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Não existe cliente com o ID informado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Não encontrado", value = """
                                    {
                                        "status": 404,
                                        "path": "/api/cliente/999",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 404, "message": "Não foi encontrado nenhum cliente com o ID: 999", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> getClienteById(@Parameter(description = "ID do cliente.", example = "2") @PathVariable("id") int id) {
        return ResponseEntity.ok(clienteService.getById(id));
    }

    @Operation(summary = "Lista todos os clientes", description = "Retorna todos os clientes cadastrados (sem a senha). Endpoint público, cortado em lotes (headers limit/batch).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso (pode vir vazia).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cliente/all",
                                        "success": true,
                                        "quantity": 2,
                                        "batch": { "quantity": 10, "loteAtual": 1, "loteTotal": 1 },
                                        "itens": [
                                            { "id": 1, "nome": "Maria da Silva", "cpfOrCnpj": "12345678900", "email": "maria@email.com", "telefone": "14999998888", "idEnderecoEntrega": 3 },
                                            { "id": 2, "nome": "João Pedro", "cpfOrCnpj": "98765432100", "email": "joao@email.com", "telefone": "14988887777", "idEnderecoEntrega": null }
                                        ]
                                    }
                                    """)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<ClienteDTO>> getAllClientes() {
        return ResponseEntity.ok(clienteService.getAllClientes());
    }

    @Operation(summary = "Cadastra um novo cliente",
            description = "Cria a conta do cliente. CPF/CNPJ e e-mail precisam ser únicos, e a senha é validada e depois criptografada antes de salvar. Endpoint público — usado antes do login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente criado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cliente/create",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 3, "nome": "Ana Costa", "cpfOrCnpj": "11122233344", "email": "ana@email.com", "telefone": "14977776666", "idEnderecoEntrega": null }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "CPF/CNPJ ou e-mail já cadastrados, ou a senha não atende aos requisitos mínimos (8+ caracteres, 1 maiúscula, 1 número, 1 caractere especial).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "CPF/CNPJ duplicado", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/cliente/create",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Já existe um cliente com esse CPF ou CNPJ cadastrado.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "E-mail duplicado", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/cliente/create",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Já existe um cliente com esse E-mail cadastrado.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "Senha fraca", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/cliente/create",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Insira alguma letra maiúscula na senha.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            }))
    })
    @PostMapping("/create")
    public ResponseEntity<ClienteDTO> createCliente(@RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.createCliente(cliente));
    }

    @Operation(summary = "Login por e-mail e senha",
            description = "Valida e-mail/senha e retorna os dados do cliente. Atenção: as chamadas seguintes usam Basic Auth com o NOME do cliente (não o e-mail). Endpoint público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login efetuado com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cliente/login",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 1, "nome": "Maria da Silva", "cpfOrCnpj": "12345678900", "email": "maria@email.com", "telefone": "14999998888", "idEnderecoEntrega": 3 }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "E-mail não cadastrado ou senha incorreta.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Credenciais inválidas", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/cliente/login",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Não foi possível efetuar o login.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PostMapping("/login")
    public ResponseEntity<ClienteDTO> login(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(clienteService.login(loginDTO));
    }

    @Operation(summary = "Atualiza os dados do cliente",
            description = "Atualiza nome, telefone e endereço de entrega. CPF/CNPJ e e-mail são imutáveis — o corpo enviado precisa repetir os valores atuais desses dois campos.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cliente/update",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 1, "nome": "Maria S. Oliveira", "cpfOrCnpj": "12345678900", "email": "maria@email.com", "telefone": "14999997777", "idEnderecoEntrega": 3 }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Tentativa de alterar outro cliente, ou de mudar o CPF/CNPJ.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = {
                                    @ExampleObject(name = "Cliente de terceiro", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/cliente/update",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Apenas é permitido alterar os próprios dados.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """),
                                    @ExampleObject(name = "CPF/CNPJ divergente", value = """
                                            {
                                                "status": 401,
                                                "path": "/api/cliente/update",
                                                "success": false,
                                                "quantity": 1,
                                                "errors": { "status": 401, "message": "Não é permitido alterar o CPF ou CNPJ de um cliente.", "hour": "2026-09-20T14:30:00" }
                                            }
                                            """)
                            }))
    })
    @PutMapping("/update")
    public ResponseEntity<ClienteDTO> updateCliente(@RequestBody ClienteDTO clienteDto, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(clienteService.updateCliente(clienteDto, idUsuarioAuth));
    }

    @Operation(summary = "Altera a senha do cliente autenticado",
            description = "Corpo esperado: { \"senha\": \"NovaSenha#123\" }. A nova senha precisa ter 8+ caracteres, 1 maiúscula, 1 número e 1 caractere especial.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cliente/change/senha",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 1, "nome": "Maria da Silva", "cpfOrCnpj": "12345678900", "email": "maria@email.com", "telefone": "14999998888", "idEnderecoEntrega": 3 }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "O campo \"senha\" não foi enviado no corpo.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Senha ausente", value = """
                                    {
                                        "status": 400,
                                        "path": "/api/cliente/change/senha",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 400, "message": "Não foi informada a senha.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "A senha enviada não atende aos requisitos mínimos.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Senha fraca", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/cliente/change/senha",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Insira uma senha maior que 8 caracteres.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PatchMapping("/change/senha")
    public ResponseEntity<ClienteDTO> updateSenha(@RequestBody Map<String, String> body, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        String senha = body.get("senha");
        if (senha.isBlank()) throw new RequisicaoIncompletaException("Não foi informada a senha.");

        return ResponseEntity.ok(clienteService.updateSenha(senha, idUsuarioAuth));
    }

    @Operation(summary = "Define o endereço de entrega do cliente",
            description = "Vincula um Endereco já cadastrado (e que pertença ao próprio cliente) como endereço de entrega.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço de entrega atualizado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Sucesso", value = """
                                    {
                                        "status": 200,
                                        "path": "/api/cliente/change/endereco/3",
                                        "success": true,
                                        "quantity": 1,
                                        "itens": [
                                            { "id": 1, "nome": "Maria da Silva", "cpfOrCnpj": "12345678900", "email": "maria@email.com", "telefone": "14999998888", "idEnderecoEntrega": 3 }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "O endereço informado não pertence ao cliente autenticado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Endereço de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/cliente/change/endereco/99",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "O endereço de entrega precisa pertencer ao cliente autenticado.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @PatchMapping("/change/endereco/{idEndereco}")
    public ResponseEntity<ClienteDTO> updateEndereco(@Parameter(description = "ID do endereço a vincular.", example = "3") @PathVariable("idEndereco") int idEndereco, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        return ResponseEntity.ok(clienteService.updateEndereco(idEndereco, idUsuarioAuth));
    }

    @Operation(summary = "Exclui a conta do cliente",
            description = "Só é permitido excluir a própria conta (idAlvo precisa ser igual ao usuário autenticado).",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta excluída com sucesso (sem corpo)."),
            @ApiResponse(responseCode = "401", description = "Tentativa de excluir a conta de outro cliente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnModel.class),
                            examples = @ExampleObject(name = "Conta de terceiro", value = """
                                    {
                                        "status": 401,
                                        "path": "/api/cliente/2",
                                        "success": false,
                                        "quantity": 1,
                                        "errors": { "status": 401, "message": "Apenas é permitido deletar a própria conta.", "hour": "2026-09-20T14:30:00" }
                                    }
                                    """)))
    })
    @DeleteMapping("/{idAlvo}")
    public ResponseEntity<Void> deleteCliente(@Parameter(description = "ID da própria conta a excluir.", example = "1") @PathVariable("idAlvo") int idAlvo, @RequestAttribute("idUsuarioAuth") int idUsuarioAuth) {
        clienteService.delete(idAlvo, idUsuarioAuth);
        return ResponseEntity.ok().build();
    }
}
