package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.CadastroClienteDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.ClienteDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static Trabalho_de_Graduacao.Mesa_do_Campo_Back.Security.ManagementHash.encriptarSenha;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteDTO getById(int id) {
        return clienteRepository.findByIdAndAtivoTrue(id)
                .map(this::entityToDTO)
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum cliente ativo com o ID: " + id));
    }

    public List<ClienteDTO> getAllClientes() {
        return clienteRepository.findAllByAtivoTrue().stream().map(this::entityToDTO).toList();
    }

    @Transactional
    public CadastroClienteDTO createCliente(Cliente cliente) {
        return clienteRepository.findByEmailIgnoreCase(cliente.getEmail())
                .map(clienteBanco -> reativarCliente(clienteBanco, cliente))
                .orElseGet(() -> criarNovoCliente(cliente));
    }

    public ClienteDTO updateCliente(ClienteDTO clienteDTO) {
        Cliente clienteBanco = clienteRepository.findByIdAndAtivoTrue(clienteDTO.id())
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum cliente ativo com o ID: " + clienteDTO.id()));

        if (!clienteBanco.getCpfOrCnpj().equals(clienteDTO.cpfOrCnpj()) || !clienteBanco.getEmail().equals(clienteDTO.email())) {
            throw new SolicitacaoNegadaException("Não é permitido alterar o CPF, CNPJ ou e-mail de um cliente.");
        }

        clienteBanco.setNome(clienteDTO.nome());
        clienteBanco.setTelefone(clienteDTO.telefone());
        clienteBanco.setIdEnderecoEntrega(clienteDTO.idEnderecoEntrega());
        return entityToDTO(clienteRepository.save(clienteBanco));
    }

    public ClienteDTO updateSenha(String senha, int idUsuarioAuth) {
        Cliente clienteBanco = clienteRepository.findByIdAndAtivoTrue(idUsuarioAuth)
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum cliente ativo com o ID: " + idUsuarioAuth));
        clienteBanco.setSenha(encriptarSenha(senha));
        return entityToDTO(clienteRepository.save(clienteBanco));
    }

    public ClienteDTO updateEndereco(int idEndereco, int idUsuarioAuth) {
        Cliente clienteBanco = clienteRepository.findByIdAndAtivoTrue(idUsuarioAuth)
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum cliente ativo com o ID: " + idUsuarioAuth));
        clienteBanco.setIdEnderecoEntrega(idEndereco);
        return entityToDTO(clienteRepository.save(clienteBanco));
    }

    @Transactional
    public void delete(int idAlvo, int idUsuarioAuth) {
        if (idUsuarioAuth != idAlvo) {
            throw new SolicitacaoNegadaException("Apenas é permitido desativar a própria conta.");
        }

        Cliente cliente = clienteRepository.findByIdAndAtivoTrue(idAlvo)
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum cliente ativo com o ID: " + idAlvo));
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    private CadastroClienteDTO criarNovoCliente(Cliente cliente) {
        if (clienteRepository.existsAnyByCpfOrCnpj(cliente.getCpfOrCnpj())) {
            throw new SolicitacaoNegadaException("Já existe um cliente com esse CPF ou CNPJ cadastrado.");
        }
        cliente.setId(0);
        cliente.setAtivo(true);
        cliente.setSenha(encriptarSenha(cliente.getSenha()));
        return new CadastroClienteDTO(entityToDTO(clienteRepository.save(cliente)), false);
    }

    private CadastroClienteDTO reativarCliente(Cliente clienteBanco, Cliente novoCadastro) {
        if (clienteBanco.isAtivo()) {
            throw new SolicitacaoNegadaException("Já existe uma conta ativa com esse e-mail.");
        }
        if (!clienteBanco.getCpfOrCnpj().equals(novoCadastro.getCpfOrCnpj())) {
            throw new SolicitacaoNegadaException("O CPF ou CNPJ informado não confere com a conta desativada.");
        }

        clienteBanco.setNome(novoCadastro.getNome());
        clienteBanco.setTelefone(novoCadastro.getTelefone());
        clienteBanco.setSenha(encriptarSenha(novoCadastro.getSenha()));
        clienteBanco.setAtivo(true);
        return new CadastroClienteDTO(entityToDTO(clienteRepository.save(clienteBanco)), true);
    }

    private ClienteDTO entityToDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(), cliente.getNome(), cliente.getCpfOrCnpj(), cliente.getEmail(),
                cliente.getTelefone(), cliente.getIdEnderecoEntrega());
    }
}
