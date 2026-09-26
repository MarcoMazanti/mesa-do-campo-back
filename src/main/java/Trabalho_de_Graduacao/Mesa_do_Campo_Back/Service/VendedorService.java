package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.CadastroVendedorDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.VendedorDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Vendedor;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ClienteRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class VendedorService {
    @Autowired
    private VendedorRepository vendedorRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    public VendedorDTO getByIdVendedor(int idVendedor) {
        Optional<Vendedor> vendedorOptional = vendedorRepository.findByIdVendedor(idVendedor);

        if (vendedorOptional.isPresent()) {
            Vendedor vendedor = vendedorOptional.get();

            Optional<Cliente> clienteOptional = clienteRepository.findByIdAndAtivoTrue(vendedor.getIdVendedor());

            if (clienteOptional.isPresent()) {
                Cliente cliente = clienteOptional.get();

                System.out.println(EntityToDTO(vendedor, cliente));
                return EntityToDTO(vendedor, cliente);
            }

            throw new RegistroInexistenteException("Não possui um cliente associado a este ID: " + idVendedor);
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhum vendedor com o ID: " + idVendedor);
    }

    public List<VendedorDTO> getAllVendedores() {
        List<Vendedor> vendedorList = vendedorRepository.findAll();

        return vendedorList.stream().map(vendedor -> {
            Optional<Cliente> clienteOptional = clienteRepository.findByIdAndAtivoTrue(vendedor.getIdVendedor());

            if (clienteOptional.isPresent()) {
                Cliente cliente = clienteOptional.get();
                return EntityToDTO(vendedor, cliente);
            }
            return null;
        }).filter(Objects::nonNull).toList();
    }

    public VendedorDTO createVendedor(Vendedor vendedor) {
        Optional<Vendedor> vendedorOptional = vendedorRepository.findByIdVendedor(vendedor.getIdVendedor());

        if (vendedorOptional.isPresent()) throw new SolicitacaoNegadaException("Já existe um vendedor com esse ID cadastrado.");

        Optional<Cliente> clienteOptional = clienteRepository.findByIdAndAtivoTrue(vendedor.getIdVendedor());

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            return EntityToDTO(vendedorRepository.save(vendedor), cliente);
        }

        Cliente cliente = clienteRepository.findByIdAndAtivoTrue(vendedor.getIdVendedor())
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum cliente ativo com o ID: " + vendedor.getIdVendedor()));

        return vendedorRepository.findByIdVendedor(vendedor.getIdVendedor())
                .map(vendedorBanco -> reativarVendedor(vendedorBanco, vendedor, cliente))
                .orElseGet(() -> criarNovoVendedor(vendedor, cliente));
    }

    public VendedorDTO updateVendedor(VendedorDTO vendedorDTO, int idUsuarioAuth) {
        if (vendedorDTO.idVendedor() == idUsuarioAuth) {
            Optional<Vendedor> vendedorOptional = vendedorRepository.findByIdVendedor(vendedorDTO.idVendedor());

            if (vendedorOptional.isPresent()) {
                Vendedor vendedorBanco = vendedorOptional.get();
                vendedorBanco.setAvaliacao(vendedorDTO.avaliacao());

                Optional<Cliente> clienteOptional = clienteRepository.findByIdAndAtivoTrue(vendedorDTO.idVendedor());

                if (clienteOptional.isPresent()) {
                    Cliente cliente = clienteOptional.get();
                    cliente.setNome(vendedorDTO.nome());
                    cliente.setEmail(vendedorDTO.email());
                    cliente.setTelefone(vendedorDTO.telefone());

        Vendedor vendedorBanco = vendedorRepository.findByIdVendedorAndAtivoTrue(vendedorDTO.idVendedor())
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum vendedor ativo com o ID: " + vendedorDTO.idVendedor()));
        Cliente cliente = clienteRepository.findByIdAndAtivoTrue(vendedorDTO.idVendedor())
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum cliente ativo com o ID: " + vendedorDTO.idVendedor()));

        vendedorBanco.setAvaliacao(vendedorDTO.avaliacao());
        cliente.setNome(vendedorDTO.nome());
        cliente.setEmail(vendedorDTO.email());
        cliente.setTelefone(vendedorDTO.telefone());

        clienteRepository.save(cliente);
        return entityToDTO(vendedorRepository.save(vendedorBanco), cliente);
    }

    @Transactional
    public void deleteVendedor(int idAlvo, int idUsuarioAuth) {
        if (idAlvo != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Apenas o vendedor pode desativar seu próprio perfil.");
        }

        Vendedor vendedor = vendedorRepository.findByIdVendedorAndAtivoTrue(idAlvo)
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum perfil de vendedor ativo com o ID: " + idAlvo));
        vendedor.setAtivo(false);
        vendedorRepository.save(vendedor);
    }

    private CadastroVendedorDTO criarNovoVendedor(Vendedor vendedor, Cliente cliente) {
        vendedor.setAtivo(true);
        return new CadastroVendedorDTO(entityToDTO(vendedorRepository.save(vendedor), cliente), false);
    }

    private CadastroVendedorDTO reativarVendedor(Vendedor vendedorBanco, Vendedor novoCadastro, Cliente cliente) {
        if (vendedorBanco.isAtivo()) {
            throw new SolicitacaoNegadaException("Você já possui um perfil de vendedor ativo.");
        }

        vendedorBanco.setContaRecebimento(novoCadastro.getContaRecebimento());
        vendedorBanco.setTipoPagamento(novoCadastro.getTipoPagamento());
        vendedorBanco.setAtivo(true);
        return new CadastroVendedorDTO(entityToDTO(vendedorRepository.save(vendedorBanco), cliente), true);
    }

    private VendedorDTO entityToDTO(Vendedor vendedor, Cliente cliente) {
        return new VendedorDTO(
                vendedor.getIdVendedor(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                vendedor.getAvaliacao(),
                vendedor.getDataAdmissao());
    }
}
