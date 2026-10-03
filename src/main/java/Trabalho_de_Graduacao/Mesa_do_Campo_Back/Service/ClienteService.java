package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.CartaoCredito;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.CartaoDebito;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ChavePix;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.CadastroClienteDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.ClienteDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.LoginDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.MetodoPagamentoDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ClienteRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.EnderecoRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.ManagementHash.encriptarSenha;
import static Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.ManagementHash.validarSenha;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private VendedorService vendedorService;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CartaoCreditoService cartaoCreditoService;
    @Autowired
    private CartaoDebitoService cartaoDebitoService;
    @Autowired
    private ChavePixService chavePixService;

    public ClienteDTO getById(int id) {
        return clienteRepository.findByIdAndAtivoTrue(id)
                .map(this::EntityToDTO)
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum cliente ativo com o ID: " + id));
    }

    public List<ClienteDTO> getAllClientes() {
        List<ClienteDTO> clienteDTOList = new ArrayList<>();

        for (Cliente cliente : clienteRepository.findAllByAtivoTrue()) {
            clienteDTOList.add(EntityToDTO(cliente));
        }

        return clienteDTOList;
    }

    public MetodoPagamentoDTO getAllMethodOfPayments(int idUsuarioAuth) {
        Optional<Cliente> clienteOptional = clienteRepository.findByIdAndAtivoTrue(idUsuarioAuth);
        if (clienteOptional.isEmpty()) throw new RegistroInexistenteException("Não foi encontrado nenhum cliente ativo com o ID: " + idUsuarioAuth);

        List<CartaoCredito> cartaoCreditos = cartaoCreditoService.getAllCartaoCreditoByIdCliente(idUsuarioAuth);
        List<CartaoDebito> cartaoDebitos = cartaoDebitoService.getAllCartaoDebitoByIdCliente(idUsuarioAuth);
        List<ChavePix> chavesPix = chavePixService.getAllChavePixByIdCliente(idUsuarioAuth);

        return new MetodoPagamentoDTO(cartaoCreditos, cartaoDebitos, chavesPix);
    }

    public void resetPassword(String email) {
        Optional<Cliente> clienteOptional = clienteRepository.findByEmailIgnoreCase(email);
        if (clienteOptional.isEmpty()) throw new RegistroInexistenteException("Não foi encontrado nenhum cliente com o e-mail: " + email);

        Cliente cliente = clienteOptional.get();
        String senhaNova = PasswordGenerator.generatePassword();
        cliente.setSenha(encriptarSenha(senhaNova));
        clienteRepository.save(cliente);

        // Mandar e-mail com nova senha
        emailService.sendEmail(cliente.getEmail(), null, "Mesa do Campo - Nova Senha", "Sua nova senha é: \"" + senhaNova + "\"", null);
    }

    @Transactional
    public CadastroClienteDTO createCliente(Cliente cliente) {
        Optional<Cliente> clienteComMesmoEmail = clienteRepository.findByEmailIgnoreCase(cliente.getEmail());

        if (clienteComMesmoEmail.isPresent()) {
            Cliente clienteBanco = clienteComMesmoEmail.get();

            if (clienteBanco.isAtivo()) {
                throw new SolicitacaoNegadaException("Já existe uma conta ativa com esse e-mail.");
            }
            if (!clienteBanco.getCpfCnpj().equals(cliente.getCpfCnpj())) {
                throw new SolicitacaoNegadaException("O CPF ou CNPJ informado não confere com a conta desativada.");
            }

            senhaValida(cliente.getSenha());
            clienteBanco.setNome(cliente.getNome());
            clienteBanco.setTelefone(cliente.getTelefone());
            clienteBanco.setSenha(encriptarSenha(cliente.getSenha()));
            clienteBanco.setAtivo(true);

            return new CadastroClienteDTO(EntityToDTO(clienteRepository.save(clienteBanco)), true);
        }

        if (clienteRepository.existsByCpfCnpj(cliente.getCpfCnpj())) {
            throw new SolicitacaoNegadaException("Já existe um cliente com esse CPF ou CNPJ cadastrado.");
        }

        senhaValida(cliente.getSenha());
        cliente.setId(0);
        cliente.setAtivo(true);
        cliente.setIdEnderecoEntrega(null);
        cliente.setSenha(encriptarSenha(cliente.getSenha()));

        return new CadastroClienteDTO(EntityToDTO(clienteRepository.save(cliente)), false);
    }

    public ClienteDTO login(LoginDTO loginDTO) {
        Optional<Cliente> clienteOptional = clienteRepository.findByEmailIgnoreCaseAndAtivoTrue(loginDTO.email());

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            if (validarSenha(loginDTO.senha(), cliente.getSenha())) {
                return EntityToDTO(cliente);
            }
        }

        throw new SolicitacaoNegadaException("Não foi possível efetuar o login.");
    }

    // CPF ou CPNJ, e E-mail são imutáveis
    public ClienteDTO updateCliente(ClienteDTO clienteDTO, int idUsuarioAuth) {
        if (clienteDTO.id() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Apenas é permitido alterar os próprios dados.");
        }
        Optional<Cliente> clienteOptional = clienteRepository.findByIdAndAtivoTrue(clienteDTO.id());
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

        Optional<Cliente> clienteOptional = clienteRepository.findByIdAndAtivoTrue(idUsuarioAuth);

        if (clienteOptional.isPresent()) {
            Cliente clienteBanco = clienteOptional.get();
            clienteBanco.setSenha(encriptarSenha(senha));
            return EntityToDTO(clienteRepository.save(clienteBanco));
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhum cliente com o ID: " + idUsuarioAuth);
    }

    public ClienteDTO updateEndereco(int idEndereco, int idUsuarioAuth) {
        if (!enderecoRepository.existsByIdAndIdUsuario(idEndereco, idUsuarioAuth)) {
            throw new SolicitacaoNegadaException("O endereço de entrega precisa pertencer ao cliente autenticado.");
        }
        Optional<Cliente> clienteOptional = clienteRepository.findByIdAndAtivoTrue(idUsuarioAuth);

        if (clienteOptional.isPresent()) {
            Cliente clienteBanco = clienteOptional.get();
            clienteBanco.setIdEnderecoEntrega(idEndereco);
            return EntityToDTO(clienteRepository.save(clienteBanco));
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhum cliente com o ID: " + idUsuarioAuth);
    }

    @Transactional
    public void delete(int idAlvo, int idUsuarioAuth) {
        if (idUsuarioAuth == idAlvo) {
            Cliente cliente = clienteRepository.findByIdAndAtivoTrue(idAlvo)
                    .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum cliente ativo com o ID: " + idAlvo));
            cliente.setAtivo(false);

            vendedorService.deleteVendedor(idAlvo, idUsuarioAuth);

            clienteRepository.save(cliente);
            return;
        }

        throw new SolicitacaoNegadaException("Apenas é permitido desativar a própria conta.");
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
