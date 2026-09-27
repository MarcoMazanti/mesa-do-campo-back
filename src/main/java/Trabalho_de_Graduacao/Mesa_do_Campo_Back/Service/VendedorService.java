package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.CadastroVendedorDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.VendedorDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Vendedor;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ClienteRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.VendedorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class VendedorService {
    @Autowired
    private VendedorRepository vendedorRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    public VendedorDTO getByIdVendedor(int idVendedor) {
        Vendedor vendedor = vendedorRepository.findByIdVendedorAndAtivoTrue(idVendedor)
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum vendedor ativo com o ID: " + idVendedor));
        Cliente cliente = clienteRepository.findByIdAndAtivoTrue(vendedor.getIdVendedor())
                .orElseThrow(() -> new RegistroInexistenteException("Não possui um cliente ativo associado a este ID: " + idVendedor));
        return EntityToDTO(vendedor, cliente);
    }

    public List<VendedorDTO> getAllVendedores() {
        return vendedorRepository.findAllByAtivoTrue().stream()
                .map(vendedor -> clienteRepository.findByIdAndAtivoTrue(vendedor.getIdVendedor())
                        .map(cliente -> EntityToDTO(vendedor, cliente))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional
    public CadastroVendedorDTO createVendedor(Vendedor vendedor, int idUsuarioAuth) {
        if (vendedor.getIdVendedor() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Apenas o próprio cliente pode criar ou reativar o perfil de vendedor.");
        }

        Cliente cliente = clienteRepository.findByIdAndAtivoTrue(vendedor.getIdVendedor())
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum cliente ativo com o ID: " + vendedor.getIdVendedor()));

        return vendedorRepository.findByIdVendedor(vendedor.getIdVendedor())
                .map(vendedorBanco -> reativarVendedor(vendedorBanco, vendedor, cliente))
                .orElseGet(() -> criarNovoVendedor(vendedor, cliente));
    }

    public VendedorDTO updateVendedor(VendedorDTO vendedorDTO, int idUsuarioAuth) {
        if (vendedorDTO.idVendedor() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Apenas o vendedor pode alterar seus dados.");
        }

        Vendedor vendedorBanco = vendedorRepository.findByIdVendedorAndAtivoTrue(vendedorDTO.idVendedor())
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum vendedor ativo com o ID: " + vendedorDTO.idVendedor()));
        Cliente cliente = clienteRepository.findByIdAndAtivoTrue(vendedorDTO.idVendedor())
                .orElseThrow(() -> new RegistroInexistenteException("Não foi encontrado nenhum cliente ativo com o ID: " + vendedorDTO.idVendedor()));

        vendedorBanco.setAvaliacao(vendedorDTO.avaliacao());
        cliente.setNome(vendedorDTO.nome());
        cliente.setEmail(vendedorDTO.email());
        cliente.setTelefone(vendedorDTO.telefone());

        clienteRepository.save(cliente);
        return EntityToDTO(vendedorRepository.save(vendedorBanco), cliente);
    }

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
        return new CadastroVendedorDTO(EntityToDTO(vendedorRepository.save(vendedor), cliente), false);
    }

    private CadastroVendedorDTO reativarVendedor(Vendedor vendedorBanco, Vendedor novoCadastro, Cliente cliente) {
        if (vendedorBanco.isAtivo()) {
            throw new SolicitacaoNegadaException("Você já possui um perfil de vendedor ativo.");
        }

        vendedorBanco.setContaRecebimento(novoCadastro.getContaRecebimento());
        vendedorBanco.setTipoPagamento(novoCadastro.getTipoPagamento());
        vendedorBanco.setAtivo(true);
        return new CadastroVendedorDTO(EntityToDTO(vendedorRepository.save(vendedorBanco), cliente), true);
    }

    private VendedorDTO EntityToDTO(Vendedor vendedor, Cliente cliente) {
        return new VendedorDTO(
                vendedor.getIdVendedor(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                vendedor.getAvaliacao(),
                vendedor.getDataAdmissao());
    }
}
