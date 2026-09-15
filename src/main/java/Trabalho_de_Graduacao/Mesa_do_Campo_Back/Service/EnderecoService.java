package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Endereco;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ClienteRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnderecoService {
    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Endereco getById(int id, int idUsuarioAuth) {
        if (enderecoRepository.existsAnyByIdAndIdUsuario(id, idUsuarioAuth)) {
            return enderecoRepository.getReferenceById(id);
        }

        throw new SolicitacaoNegadaException("O Usuário está tentando acessar um endereço que não pertence a ele.");
    }

    public List<Endereco> getByUsuario(int idUsuario) {
        return enderecoRepository.findAllByIdUsuario(idUsuario);
    }

    public Endereco create(Endereco endereco, int idUsuarioAuth) {
        // O usuário autenticado é a fonte de verdade; nunca aceite o dono
        // informado pelo corpo da requisição.
        endereco.setId(0);
        endereco.setIdUsuario(idUsuarioAuth);
        return enderecoRepository.save(endereco);
    }

    public Endereco update(Endereco endereco, int idUsuarioAuth) {
        return enderecoRepository.findById(endereco.getId())
                .filter(enderecoBanco -> enderecoBanco.getIdUsuario() == idUsuarioAuth)
                .map(enderecoBanco -> {
                    enderecoBanco.setCep(endereco.getCep());
                    enderecoBanco.setCountry(endereco.getCountry());
                    enderecoBanco.setState(endereco.getState());
                    enderecoBanco.setCity(endereco.getCity());
                    enderecoBanco.setAdress(endereco.getAdress());
                    enderecoBanco.setNumber(endereco.getNumber());
                    enderecoBanco.setComplement(endereco.getComplement());
                    return enderecoRepository.save(enderecoBanco);
                })
                .orElseThrow(() -> new SolicitacaoNegadaException("O Usuário está tentando acessar um endereço que não pertence a ele."));
    }

    @Transactional
    public void delete(int id, int idUsuarioAuth) {
        if (enderecoRepository.existsByIdAndIdUsuario(id, idUsuarioAuth)) {
            clienteRepository.findById(idUsuarioAuth).ifPresent(cliente -> {
                if (Integer.valueOf(id).equals(cliente.getIdEnderecoEntrega())) {
                    cliente.setIdEnderecoEntrega(null);
                    clienteRepository.save(cliente);
                }
            });
            enderecoRepository.deleteById(id);
            return;
        }

        throw new SolicitacaoNegadaException("O Usuário está tentando acessar um endereço que não pertence a ele.");
    }
}
