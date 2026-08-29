package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Endereco;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnderecoService {
    @Autowired
    private EnderecoRepository enderecoRepository;

    public Endereco getById(int id, int idUsuarioAuth) {
        if (enderecoRepository.existsAnyByIdAndIdUsuario(id, idUsuarioAuth)) {
            return enderecoRepository.getReferenceById(id);
        }

        throw new SolicitacaoNegadaException("O Usuário está tentando acessar um endereço que não pertence a ele.");
    }

    public List<Endereco> getByUsuario(int idUsuario) {
        return enderecoRepository.findAllByIdUsuario(idUsuario);
    }

    public Endereco create(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public Endereco update(Endereco endereco, int idUsuarioAuth) {
        if (enderecoRepository.existsByIdAndIdUsuario(endereco.getId(), idUsuarioAuth)) {
            return enderecoRepository.save(endereco);
        }

        throw new SolicitacaoNegadaException("O Usuário está tentando acessar um endereço que não pertence a ele.");
    }

    public void delete(int id, int idUsuarioAuth) {
        if (enderecoRepository.existsByIdAndIdUsuario(id, idUsuarioAuth)) {
            enderecoRepository.deleteById(id);
        }

        throw new SolicitacaoNegadaException("O Usuário está tentando acessar um endereço que não pertence a ele.");
    }
}
