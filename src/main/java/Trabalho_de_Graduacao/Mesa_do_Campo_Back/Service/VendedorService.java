package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.VendedorDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Vendedor;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ClienteRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendedorService {
    @Autowired
    private VendedorRepository vendedorRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    public VendedorDTO getByIdVendedor(int idVendedor) {
        Optional<Vendedor> vendedorOptional = vendedorRepository.findByIDVendedor(idVendedor);

        if (vendedorOptional.isPresent()) {
            Vendedor vendedor = vendedorOptional.get();

            Optional<Cliente> clienteOptional = clienteRepository.findById(vendedor.getIdVendedor());

            if (clienteOptional.isPresent()) {
                Cliente cliente = clienteOptional.get();

                return EntityToDTO(vendedor, cliente);
            }

            throw new RegistroInexistenteException("Não possui um cliente associado a este ID: " + idVendedor);
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhum vendedor com o ID: " + idVendedor);
    }

    public List<VendedorDTO> getAllVendedores() {
        List<Vendedor> vendedorList = vendedorRepository.findAll();

        return vendedorList.stream().map(vendedor -> {
            Optional<Cliente> clienteOptional = clienteRepository.findById(vendedor.getIdVendedor());

            if (clienteOptional.isPresent()) {
                Cliente cliente = clienteOptional.get();
                return EntityToDTO(vendedor, cliente);
            }
            return null;
        }).toList();
    }

    public VendedorDTO createVendedor(Vendedor vendedor) {
        Optional<Vendedor> vendedorOptional = vendedorRepository.findByIDVendedor(vendedor.getIdVendedor());

        if (vendedorOptional.isPresent()) throw new SolicitacaoNegadaException("Já existe um vendedor com esse ID cadastrado.");

        Optional<Cliente> clienteOptional = clienteRepository.findById(vendedor.getIdVendedor());

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            return EntityToDTO(vendedorRepository.save(vendedor), cliente);
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhum cliente com o ID: " + vendedor.getIdVendedor());
    }

    public VendedorDTO updateVendedor(VendedorDTO vendedorDTO, int idUsuarioAuth) {
        if (vendedorDTO.idVendedor() == idUsuarioAuth) {
            Optional<Vendedor> vendedorOptional = vendedorRepository.findByIDVendedor(vendedorDTO.idVendedor());

            if (vendedorOptional.isPresent()) {
                Vendedor vendedorBanco = vendedorOptional.get();
                vendedorBanco.setAvaliacao(vendedorDTO.avaliacao());

                Optional<Cliente> clienteOptional = clienteRepository.findById(vendedorDTO.idVendedor());

                if (clienteOptional.isPresent()) {
                    Cliente cliente = clienteOptional.get();
                    cliente.setNome(vendedorDTO.nome());
                    cliente.setEmail(vendedorDTO.email());
                    cliente.setTelefone(vendedorDTO.telefone());

                    clienteRepository.save(cliente);

                    return EntityToDTO(vendedorRepository.save(vendedorBanco), cliente);
                }

                throw new RegistroInexistenteException("Não foi encontrado nenhum cliente com o ID: " + vendedorDTO.idVendedor());
            }

            throw new RegistroInexistenteException("Não foi encontrado nenhum vendedor com o ID: " + vendedorDTO.idVendedor());
        }

        throw new SolicitacaoNegadaException("Apenas o vendedor pode alterar seus dados.");
    }

    public void deleteVendedor(int idAlvo, int idUsuarioAuth) {
        if (idAlvo == idUsuarioAuth) {
            vendedorRepository.deleteById(idAlvo);
            return;
        }

        throw new SolicitacaoNegadaException("Apenas o vendedor pode excluir seus dados.");
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
