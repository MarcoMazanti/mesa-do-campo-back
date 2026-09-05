package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.TipoPagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.PagamentoRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Transactional
    public Pagamento criarPagamento(int idPedido, TipoPagamento metodoPagamento, int idUsuarioAuth) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RegistroInexistenteException("Pedido não encontrado."));

        if (pedido.getIdUsuario() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Este pedido não pertence ao usuário.");
        }

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new SolicitacaoNegadaException("Não é possível pagar um pedido cancelado.");
        }

        if (pagamentoRepository.findByIdPedido(idPedido).isPresent()) {
            throw new SolicitacaoNegadaException("Este pedido já possui um pagamento registrado.");
        }

        Pagamento pagamento = new Pagamento(
                idPedido,
                metodoPagamento,
                StatusPagamento.APROVADO, // simulado — sem gateway de pagamento real
                LocalDateTime.now(),
                pedido.getPrecoTotal()
        );

        Pagamento salvo = pagamentoRepository.save(pagamento);

        if (pedido.getStatus() == StatusPedido.AGUARDANDO_PAGAMENTO) {
            pedido.setStatus(StatusPedido.PROCESSANDO);
            pedidoRepository.save(pedido);
        }

        return salvo;
    }

    @Transactional
    public Pagamento atualizarStatus(int id, StatusPagamento novoStatus, int idUsuarioAuth) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new RegistroInexistenteException("Pagamento não encontrado."));

        Pedido pedido = pedidoRepository.findById(pagamento.getIdPedido())
                .orElseThrow(() -> new RegistroInexistenteException("Pedido não encontrado."));

        if (pedido.getIdUsuario() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Não é possível alterar esse pagamento.");
        }

        pagamento.setStatus(novoStatus);
        if (novoStatus == StatusPagamento.APROVADO && pagamento.getDataPagamento() == null) {
            pagamento.setDataPagamento(LocalDateTime.now());
        }
        Pagamento salvo = pagamentoRepository.save(pagamento);

        // Só mexe no pedido se ele ainda estiver "em andamento" - não reabre
        // um pedido que já foi entregue ou cancelado por outro motivo.
        if (pedido.getStatus() != StatusPedido.ENTREGUE && pedido.getStatus() != StatusPedido.CANCELADO) {
            switch (novoStatus) {
                case APROVADO -> pedido.setStatus(StatusPedido.PROCESSANDO);
                case RECUSADO -> pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
                case CANCELADO, ESTORNADO -> pedido.setStatus(StatusPedido.CANCELADO);
                default -> pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
            }
            pedidoRepository.save(pedido);
        }

        return salvo;
    }
}
