package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Avaliacao;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    public Avaliacao getById(int id) {
        Optional<Avaliacao> avaliacaoOptional = avaliacaoRepository.findById(id);

        if (avaliacaoOptional.isPresent()) {
            return avaliacaoOptional.get();
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhuma avaliação com o ID: " + id);
    }

    public List<Avaliacao> getAllAvaliacoesByIdVendedor(int idVendedor) {
        List<Avaliacao> avaliacaoList = avaliacaoRepository.findAllByVendedorId(idVendedor);

        if (avaliacaoList.isEmpty()) throw new RegistroInexistenteException("Não foi encontrado nenhuma avaliação relacionadas ao vendedor de ID: " + idVendedor);

        return avaliacaoList;
    }

    public List<Avaliacao> getAllAvaliacoesByIdCliente(int idCliente) {
        List<Avaliacao> avaliacaoList = avaliacaoRepository.findAllByClienteId(idCliente);

        if (avaliacaoList.isEmpty()) throw new RegistroInexistenteException("Não foi encontrado nenhuma avaliação enviada pelo cliente de ID: " + idCliente);

        return avaliacaoList;
    }

    public List<Avaliacao> getAllAvaliacoes() {
        List<Avaliacao> avaliacaoList = avaliacaoRepository.findAll();

        if (avaliacaoList.isEmpty()) throw new RegistroInexistenteException("Não foi encontrado nenhuma avaliação registrada.");

        return avaliacaoRepository.findAll();
    }

    public Avaliacao createAvaliacao(Avaliacao avaliacao, int idUsuarioAuth) {
        if (avaliacao.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não é permitido utilizar um ID distinto do seu.");
        if (avaliacao.getIdVendedor() == avaliacao.getIdCliente()) throw new SolicitacaoNegadaException("Não é permitido se auto avaliar.");

        return avaliacaoRepository.save(avaliacao);
    }

    public Avaliacao updateAvaliacao(Avaliacao avaliacao, int idUsuarioAuth) {
        Optional<Avaliacao> avaliacaoOptional = avaliacaoRepository.findById(avaliacao.getId());

        if (avaliacaoOptional.isPresent()) {
            if (avaliacao.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não é permitido utilizar um ID distinto do seu.");
            if (avaliacao.getIdVendedor() == avaliacao.getIdCliente()) throw new SolicitacaoNegadaException("Não é permitido se auto avaliar.");

            Avaliacao avaliacaoBanco = avaliacaoOptional.get();

            avaliacaoBanco.setNota(avaliacao.getNota());
            avaliacaoBanco.setDescricao(avaliacao.getDescricao());

            return avaliacaoRepository.save(avaliacaoBanco);
        }

        throw new RegistroInexistenteException("Não foi encontrado a avaliação com o ID: " + avaliacao.getId() + " para atualizar.");
    }

    public void deleteAvaliacao(int idAlvo, int idUsuarioAuth) {
        Optional<Avaliacao> avaliacaoOptional = avaliacaoRepository.findById(idAlvo);

        if (avaliacaoOptional.isPresent()) {
            Avaliacao avaliacaoBanco = avaliacaoOptional.get();

            if (avaliacaoBanco.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não é permitido deletar uma avaliação diferente do seu.");

            avaliacaoRepository.deleteById(idAlvo);
            return;
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhuma avaliação com o ID: " + idAlvo + " para excluir.");
    }
}
