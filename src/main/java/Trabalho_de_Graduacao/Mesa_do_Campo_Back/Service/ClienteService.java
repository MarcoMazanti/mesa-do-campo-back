package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.ClienteDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static Trabalho_de_Graduacao.Mesa_do_Campo_Back.Security.ManagementHash.encriptarSenha;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteDTO getById(int id) {
        if (clienteRepository.existsById(id)) {
            return EntityToDTO(clienteRepository.getReferenceById(id));
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhum cliente com o ID: " + id);
    }

    public List<ClienteDTO> getAllClientes() {
        List<ClienteDTO> clienteDTOList = new ArrayList<>();

        for (Cliente cliente : clienteRepository.findAll()) {
            clienteDTOList.add(EntityToDTO(cliente));
        }

        return clienteDTOList;
    }

    public ClienteDTO createCliente(Cliente cliente) {
        if (clienteRepository.existsAnyByCpfOrCnpj(cliente.getCpfOrCnpj())) {
            throw new SolicitacaoNegadaException("Já existe um cliente com esse CPF ou CNPJ cadastrado.");
        }

        if (clienteRepository.existsAnyByEmail(cliente.getEmail())) {
            throw new SolicitacaoNegadaException("Já existe um cliente com esse E-mail cadastrado.");
        }

        return EntityToDTO(clienteRepository.save(cliente));
    }

    // CPF ou CPNJ, e E-mail são imutáveis
    public ClienteDTO updateCliente(ClienteDTO clienteDTO) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteDTO.id());
        if (clienteOptional.isPresent()) {
            Cliente clienteBanco = clienteOptional.get();

            // Comparação dos campos imutáveis
            if (clienteBanco.getCpfOrCnpj().equals(clienteDTO.cpfOrCnpj()) && clienteBanco.getEmail().equals(clienteDTO.email())) {
                clienteBanco.setNome(clienteDTO.nome());
                clienteBanco.setTelefone(clienteDTO.telefone());
                clienteBanco.setIdEnderecoEntrega(clienteDTO.idEnderecoEntrega());

                return EntityToDTO(clienteRepository.save(clienteBanco));
            }
        }

        throw new SolicitacaoNegadaException("Não é permitido alterar o CPF ou CNPJ de um cliente.");
    }

    public ClienteDTO updateSenha(String senha, int idUsuarioAuth) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(idUsuarioAuth);

        if (clienteOptional.isPresent()) {
            Cliente clienteBanco = clienteOptional.get();
            clienteBanco.setSenha(encriptarSenha(senha));
            return EntityToDTO(clienteRepository.save(clienteBanco));
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhum cliente com o ID: " + idUsuarioAuth);
    }

    public ClienteDTO updateEndereco(int idEndereco, int idUsuarioAuth) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(idUsuarioAuth);

        if (clienteOptional.isPresent()) {
            Cliente clienteBanco = clienteOptional.get();
            clienteBanco.setIdEnderecoEntrega(idEndereco);
            return EntityToDTO(clienteRepository.save(clienteBanco));
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhum cliente com o ID: " + idUsuarioAuth);
    }

    public void delete(int idAlvo, int idUsuarioAuth) {
        if (idUsuarioAuth == idAlvo) {
            if (clienteRepository.existsById(idAlvo)) {
                clienteRepository.deleteById(idAlvo);
                return;
            }

            throw new RegistroInexistenteException("Não foi encontrado nenhum cliente com o ID: " + idAlvo);
        }

        throw new SolicitacaoNegadaException("Apenas é permitido deletar a própria conta.");
    }

    private ClienteDTO EntityToDTO(Cliente cliente) {
        return new ClienteDTO(cliente.getId(),
                cliente.getNome(),
                cliente.getCpfOrCnpj(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getIdEnderecoEntrega());
    }
}
