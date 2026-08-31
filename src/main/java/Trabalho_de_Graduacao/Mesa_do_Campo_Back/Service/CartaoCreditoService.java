package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.CartaoCredito;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.CartaoCreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartaoCreditoService {
    @Autowired
    private CartaoCreditoRepository cartaoCreditoRepository;

    public CartaoCredito getById(int id, int idUsuarioAuth) {
        Optional<CartaoCredito> cartaoCreditoOptional = cartaoCreditoRepository.findById(id);

        if (cartaoCreditoOptional.isPresent()) {
            CartaoCredito cartaoCredito = cartaoCreditoOptional.get();

            if (cartaoCredito.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Apenas o cliente pode visualizar seus dados.");
            return cartaoCreditoOptional.get();
        }

        throw new RegistroInexistenteException("Não foi eocontrado um cartão de crédito com o ID: " + id);
    }

    public List<CartaoCredito> getAllCartaoCreditoByIdCliente(int idUsuarioAuth) {
        List<CartaoCredito> cartaoCreditoList = cartaoCreditoRepository.findAllByClienteId(idUsuarioAuth);

        if (cartaoCreditoList.isEmpty()) throw new RegistroInexistenteException("Não possui nenhum cartão de crédito cadastrado para este usuário.");

        return cartaoCreditoList;
    }

    public CartaoCredito getCartaoCreditoAtivo(int idUsuarioAuth) {
        List<CartaoCredito> cartaoCreditoList = cartaoCreditoRepository.findAllByClienteId(idUsuarioAuth);

        if (cartaoCreditoList.isEmpty()) throw new RegistroInexistenteException("Não possui nenhum cartão de crédito cadastrado para este usuário.");

        for (CartaoCredito cartaoCredito : cartaoCreditoList) {
            if (cartaoCredito.isPadrao()) return cartaoCredito;
        }

        throw new RegistroInexistenteException("Não possui nenhum cartão de crédito ativo para o cliente com o ID: " + idUsuarioAuth);
    }

    public CartaoCredito createCartaoCredito(CartaoCredito cartaoCredito, int idUsuarioAuth) {
        if (cartaoCredito.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não se pode criar um cartão para um terceiro.");

        return cartaoCreditoRepository.save(cartaoCredito);
    }

    public CartaoCredito updateCartaoCredito(CartaoCredito cartaoCredito, int idUsuarioAuth) {
        if (cartaoCredito.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não se pode criar um cartão para um terceiro.");

        Optional<CartaoCredito> cartaoCreditoOptional = cartaoCreditoRepository.findById(cartaoCredito.getId());

        if (cartaoCreditoOptional.isPresent()) {
            CartaoCredito cartaoCreditoBanco = cartaoCreditoOptional.get();

            if (cartaoCreditoBanco.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não se pode criar um cartão para um terceiro.");

            cartaoCreditoBanco.setNome(cartaoCredito.getNome());
            cartaoCreditoBanco.setBandeira(cartaoCredito.getBandeira());
            cartaoCreditoBanco.setUltimosDigitos(cartaoCredito.getUltimosDigitos());
            cartaoCreditoBanco.setTokenGateway(cartaoCredito.getTokenGateway());

            return cartaoCreditoRepository.save(cartaoCreditoBanco);
        }

        throw new RegistroInexistenteException("Não foi encontrado o cartão solicitado para atualizar.");
    }

    public CartaoCredito ativarCartaoCredito(int id, int idUsuarioAuth) {
        List<CartaoCredito> cartaoCreditoList = getAllCartaoCreditoByIdCliente(idUsuarioAuth);

        for (CartaoCredito cartaoCredito : cartaoCreditoList) {
            if (cartaoCredito.isPadrao()) {
                cartaoCredito.setPadrao(false);
                cartaoCreditoRepository.save(cartaoCredito);
            }
        }

        for (CartaoCredito cartaoCredito : cartaoCreditoList) {
            if (cartaoCredito.getId() == id) {
                cartaoCredito.setPadrao(true);
                return cartaoCreditoRepository.save(cartaoCredito);
            }
        }

        throw new RegistroInexistenteException("Não foi encontrado um cartão de crédito com o ID: " + id + "para o cliente com o ID: " + idUsuarioAuth + " ativar.");
    }

    public void desativarCartaoCredito(int id, int idUsuarioAuth) {
        List<CartaoCredito> cartaoCreditoList = getAllCartaoCreditoByIdCliente(idUsuarioAuth);

        for (CartaoCredito cartaoCredito : cartaoCreditoList) {
            if (cartaoCredito.getId() == id) {
                cartaoCredito.setPadrao(false);
                cartaoCreditoRepository.save(cartaoCredito);
                return;
            }
        }

        throw new RegistroInexistenteException("Não foi encontrado um cartão de crédito com o ID: " + id + "para o cliente com o ID: " + idUsuarioAuth + " desativar.");
    }

    public void deleteCartaoCredito(int id, int idUsuarioAuth) {
        Optional<CartaoCredito> cartaoCreditoOptional = cartaoCreditoRepository.findById(id);

        if (cartaoCreditoOptional.isPresent()) {
            CartaoCredito cartaoCreditoBanco = cartaoCreditoOptional.get();

            if (cartaoCreditoBanco.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não se pode deletar um cartão de terceiro");

            cartaoCreditoRepository.deleteById(id);
        }

        throw new RegistroInexistenteException("Não foi encontrado um cartão de crédito com o ID: " + id + "para o cliente com o ID: " + idUsuarioAuth + " para deletar.");
    }
}
