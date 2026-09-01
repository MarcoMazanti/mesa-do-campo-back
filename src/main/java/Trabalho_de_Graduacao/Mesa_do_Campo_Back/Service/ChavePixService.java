package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ChavePix;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ChavePixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChavePixService {
    @Autowired
    private ChavePixRepository chavePixRepository;

    public ChavePix getById(int id, int idUsuarioAuth) {
        Optional<ChavePix> chavePixOptional = chavePixRepository.findById(id);

        if (chavePixOptional.isPresent()) {
            ChavePix chavePix = chavePixOptional.get();

            if (chavePix.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Apenas o cliente pode visualizar seus dados.");
            return chavePixOptional.get();
        }

        throw new RegistroInexistenteException("Não foi eocontrado uma chave pix com o ID: " + id);
    }

    public List<ChavePix> getAllChavePixByIdCliente(int idUsuarioAuth) {
        List<ChavePix> chavePixList = chavePixRepository.findAllByClienteId(idUsuarioAuth);

        if (chavePixList.isEmpty()) throw new RegistroInexistenteException("Não possui nenhuma chave pix cadastrada para este usuário.");

        return chavePixList;
    }

    public ChavePix getChavePixAtivo(int idUsuarioAuth) {
        List<ChavePix> chavePixList = chavePixRepository.findAllByClienteId(idUsuarioAuth);

        if (chavePixList.isEmpty()) throw new RegistroInexistenteException("Não possui nenhuma chave pix cadastrada para este usuário.");

        for (ChavePix chavePix : chavePixList) {
            if (chavePix.isPadrao()) return chavePix;
        }

        throw new RegistroInexistenteException("Não possui nenhuma chave pix ativa para o cliente com o ID: " + idUsuarioAuth);
    }

    public ChavePix createChavePix(ChavePix chavePix, int idUsuarioAuth) {
        if (chavePix.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não se pode criar uma chave para um terceiro.");

        return chavePixRepository.save(chavePix);
    }

    public ChavePix updateChavePix(ChavePix chavePix, int idUsuarioAuth) {
        if (chavePix.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não se pode criar uma chave para um terceiro.");

        Optional<ChavePix> chavePixOptional = chavePixRepository.findById(chavePix.getId());

        if (chavePixOptional.isPresent()) {
            ChavePix chavePixBanco = chavePixOptional.get();

            if (chavePixBanco.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não se pode criar uma chave para um terceiro.");

            chavePixBanco.setNome(chavePix.getNome());
            chavePixBanco.setTipoChave(chavePix.getTipoChave());
            chavePixBanco.setChave(chavePix.getChave());

            return chavePixRepository.save(chavePixBanco);
        }

        throw new RegistroInexistenteException("Não foi encontrado a chave pix solicitada para atualizar.");
    }

    public ChavePix ativarChavePix(int id, int idUsuarioAuth) {
        List<ChavePix> chavePixList = getAllChavePixByIdCliente(idUsuarioAuth);

        for (ChavePix chavePix : chavePixList) {
            if (chavePix.isPadrao()) {
                chavePix.setPadrao(false);
                chavePixRepository.save(chavePix);
            }
        }

        for (ChavePix chavePix : chavePixList) {
            if (chavePix.getId() == id) {
                chavePix.setPadrao(true);
                return chavePixRepository.save(chavePix);
            }
        }

        throw new RegistroInexistenteException("Não foi encontrado uma chave pix com o ID: " + id + "para o cliente com o ID: " + idUsuarioAuth + " ativar.");
    }

    public void desativarChavePix(int id, int idUsuarioAuth) {
        List<ChavePix> chavePixList = getAllChavePixByIdCliente(idUsuarioAuth);

        for (ChavePix chavePix : chavePixList) {
            if (chavePix.getId() == id) {
                chavePix.setPadrao(false);
                chavePixRepository.save(chavePix);
                return;
            }
        }

        throw new RegistroInexistenteException("Não foi encontrado uma chave pix com o ID: " + id + "para o cliente com o ID: " + idUsuarioAuth + " desativar.");
    }

    public void deleteChavePix(int id, int idUsuarioAuth) {
        Optional<ChavePix> chavePixOptional = chavePixRepository.findById(id);

        if (chavePixOptional.isPresent()) {
            ChavePix chavePixBanco = chavePixOptional.get();

            if (chavePixBanco.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não se pode deletar uma chave de terceiro");

            chavePixRepository.deleteById(id);
        }

        throw new RegistroInexistenteException("Não foi encontrado uma chave pix com o ID: " + id + "para o cliente com o ID: " + idUsuarioAuth + " para deletar.");
    }
}
