package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.PagamentoRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagamentoService {
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private PedidoRepository pedidoRepository;

    public Pagamento findById(int id, int idUsuarioAuth) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new RegistroInexistenteException("Pagamento não encontrado."));

        Pedido pedido = pedidoRepository.findById(pagamento.getIdPedido())
                .orElseThrow(() -> new RegistroInexistenteException("Pedido não encontrado."));

        if (pedido.getIdUsuario() != idUsuarioAuth) throw new SolicitacaoNegadaException("Não é possível acessar esse pagamento.");

        return pagamento;
    }

    public List<Pagamento> findAllByUsuario(int idUsuarioAuth) {
        List<Pagamento> pagamentoList = pagamentoRepository.findAllByIdUsuario(idUsuarioAuth);

        if (pagamentoList.isEmpty()) throw new RegistroInexistenteException("Nenhum pagamento encontrado para o usuário.");

        return pagamentoList;
    }
}
