package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.ClienteDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.LoginDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static Trabalho_de_Graduacao.Mesa_do_Campo_Back.Security.ManagementHash.encriptarSenha;
import static Trabalho_de_Graduacao.Mesa_do_Campo_Back.Security.ManagementHash.validarSenha;

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
        if (clienteRepository.existsByCpfCnpj(cliente.getCpfCnpj())) {
            throw new SolicitacaoNegadaException("Já existe um cliente com esse CPF ou CNPJ cadastrado.");
        }

        if (clienteRepository.existsAnyByEmail(cliente.getEmail())) {
            throw new SolicitacaoNegadaException("Já existe um cliente com esse E-mail cadastrado.");
        }

        senhaValida(cliente.getSenha());

        return EntityToDTO(clienteRepository.save(cliente));
    }

    public ClienteDTO login(LoginDTO loginDTO) {
        List<Cliente> clienteList = clienteRepository.findAllByNome(loginDTO.nome());

        for (Cliente cliente : clienteList) {
            if (cliente.getNome().equals(loginDTO.nome())) {
                if (validarSenha(loginDTO.senha(), cliente.getSenha())) {
                    return EntityToDTO(cliente);
                }
            }
        }

        throw new SolicitacaoNegadaException("Não foi possível efetuar o login.");
    }

    // CPF ou CPNJ, e E-mail são imutáveis
    public ClienteDTO updateCliente(ClienteDTO clienteDTO) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteDTO.id());
        if (clienteOptional.isPresent()) {
            Cliente clienteBanco = clienteOptional.get();

            // Comparação dos campos imutáveis
            if (clienteBanco.getCpfCnpj().equals(clienteDTO.cpfOrCnpj()) && clienteBanco.getEmail().equals(clienteDTO.email())) {
                clienteBanco.setNome(clienteDTO.nome());
                clienteBanco.setTelefone(clienteDTO.telefone());
                clienteBanco.setIdEnderecoEntrega(clienteDTO.idEnderecoEntrega());

                return EntityToDTO(clienteRepository.save(clienteBanco));
            }
        }

        throw new SolicitacaoNegadaException("Não é permitido alterar o CPF ou CNPJ de um cliente.");
    }

    public ClienteDTO updateSenha(String senha, int idUsuarioAuth) {
        senhaValida(senha);

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

    private void senhaValida(String senha) {
        if (senha.isBlank()) throw new SolicitacaoNegadaException("Informe a senha para prosseguir.");
        if (senha.length() < 8) throw new SolicitacaoNegadaException("Insira uma senha maior que 8 caracteres.");
        if (!Pattern.compile("[A-Z]").matcher(senha).find()) throw new SolicitacaoNegadaException("Insira alguma letra maiúscula na senha.");
        if (!Pattern.compile("\\d").matcher(senha).find()) throw new SolicitacaoNegadaException("Insira algum número na senha.");
        if (!Pattern.compile("[^a-zA-Z0-9]").matcher(senha).find()) throw new SolicitacaoNegadaException("Insira algum caractere especial na senha.");
    }

    private ClienteDTO EntityToDTO(Cliente cliente) {
        return new ClienteDTO(cliente.getId(),
                cliente.getNome(),
                cliente.getCpfCnpj(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getIdEnderecoEntrega());
    }
}
