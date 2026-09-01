package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.CartaoDebito;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.CartaoDebitoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartaoDebitoService {
    @Autowired
    private CartaoDebitoRepository cartaoDebitoRepository;

    public CartaoDebito getById(int id, int idUsuarioAuth) {
        Optional<CartaoDebito> cartaoDebitoOptional = cartaoDebitoRepository.findById(id);

        if (cartaoDebitoOptional.isPresent()) {
            CartaoDebito cartaoDebito = cartaoDebitoOptional.get();

            if (cartaoDebito.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Apenas o cliente pode visualizar seus dados.");
            return cartaoDebitoOptional.get();
        }

        throw new RegistroInexistenteException("Não foi eocontrado um cartão de débito com o ID: " + id);
    }

    public List<CartaoDebito> getAllCartaoDebitoByIdCliente(int idUsuarioAuth) {
        List<CartaoDebito> cartaoDebitoList = cartaoDebitoRepository.findAllByClienteId(idUsuarioAuth);

        if (cartaoDebitoList.isEmpty()) throw new RegistroInexistenteException("Não possui nenhum cartão de débito cadastrado para este usuário.");

        return cartaoDebitoList;
    }

    public CartaoDebito getCartaoDebitoAtivo(int idUsuarioAuth) {
        List<CartaoDebito> cartaoDebitoList = cartaoDebitoRepository.findAllByClienteId(idUsuarioAuth);

        if (cartaoDebitoList.isEmpty()) throw new RegistroInexistenteException("Não possui nenhum cartão de débito cadastrado para este usuário.");

        for (CartaoDebito cartaoDebito : cartaoDebitoList) {
            if (cartaoDebito.isPadrao()) return cartaoDebito;
        }

        throw new RegistroInexistenteException("Não possui nenhum cartão de débito ativo para o cliente com o ID: " + idUsuarioAuth);
    }

    public CartaoDebito createCartaoDebito(CartaoDebito cartaoDebito, int idUsuarioAuth) {
        if (cartaoDebito.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não se pode criar um cartão para um terceiro.");

        return cartaoDebitoRepository.save(cartaoDebito);
    }

    public CartaoDebito updateCartaoDebito(CartaoDebito cartaoDebito, int idUsuarioAuth) {
        if (cartaoDebito.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não se pode criar um cartão para um terceiro.");

        Optional<CartaoDebito> cartaoDebitoOptional = cartaoDebitoRepository.findById(cartaoDebito.getId());

        if (cartaoDebitoOptional.isPresent()) {
            CartaoDebito cartaoDebitoBanco = cartaoDebitoOptional.get();

            if (cartaoDebitoBanco.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não se pode criar um cartão para um terceiro.");

            cartaoDebitoBanco.setNome(cartaoDebito.getNome());
            cartaoDebitoBanco.setBandeira(cartaoDebito.getBandeira());
            cartaoDebitoBanco.setUltimosDigitos(cartaoDebito.getUltimosDigitos());
            cartaoDebitoBanco.setTokenGateway(cartaoDebito.getTokenGateway());

            return cartaoDebitoRepository.save(cartaoDebitoBanco);
        }

        throw new RegistroInexistenteException("Não foi encontrado o cartão solicitado para atualizar.");
    }

    public CartaoDebito ativarCartaoDebito(int id, int idUsuarioAuth) {
        List<CartaoDebito> cartaoDebitoList = getAllCartaoDebitoByIdCliente(idUsuarioAuth);

        for (CartaoDebito cartaoDebito : cartaoDebitoList) {
            if (cartaoDebito.isPadrao()) {
                cartaoDebito.setPadrao(false);
                cartaoDebitoRepository.save(cartaoDebito);
            }
        }

        for (CartaoDebito cartaoDebito : cartaoDebitoList) {
            if (cartaoDebito.getId() == id) {
                cartaoDebito.setPadrao(true);
                return cartaoDebitoRepository.save(cartaoDebito);
            }
        }

        throw new RegistroInexistenteException("Não foi encontrado um cartão de débito com o ID: " + id + "para o cliente com o ID: " + idUsuarioAuth + " ativar.");
    }

    public void desativarCartaoDebito(int id, int idUsuarioAuth) {
        List<CartaoDebito> cartaoDebitoList = getAllCartaoDebitoByIdCliente(idUsuarioAuth);

        for (CartaoDebito cartaoDebito : cartaoDebitoList) {
            if (cartaoDebito.getId() == id) {
                cartaoDebito.setPadrao(false);
                cartaoDebitoRepository.save(cartaoDebito);
                return;
            }
        }

        throw new RegistroInexistenteException("Não foi encontrado um cartão de débito com o ID: " + id + "para o cliente com o ID: " + idUsuarioAuth + " desativar.");
    }

    public void deleteCartaoDebito(int id, int idUsuarioAuth) {
        Optional<CartaoDebito> cartaoDebitoOptional = cartaoDebitoRepository.findById(id);

        if (cartaoDebitoOptional.isPresent()) {
            CartaoDebito cartaoDebitoBanco = cartaoDebitoOptional.get();

            if (cartaoDebitoBanco.getIdCliente() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não se pode deletar um cartão de terceiro");

            cartaoDebitoRepository.deleteById(id);
        }

        throw new RegistroInexistenteException("Não foi encontrado um cartão de débito com o ID: " + id + "para o cliente com o ID: " + idUsuarioAuth + " para deletar.");
    }
}
