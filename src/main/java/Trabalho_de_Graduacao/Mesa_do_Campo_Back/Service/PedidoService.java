package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Produto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ItemPedidoRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.PagamentoRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.PedidoRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private PagamentoRepository pagamentoRepository;

    public List<Pedido> getAllPedidosByUsuario(int idUsuarioAuth) {
        List<Pedido> pedidos = pedidoRepository.findAllByIdUsuario(idUsuarioAuth);

        if (pedidos.isEmpty()) throw new RegistroInexistenteException("Não existem pedidos para o usuário.");

        return pedidos;
    }

    public Pedido setPedidoEntregue(int id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RegistroInexistenteException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.CANCELADO) {
            pedido.setStatus(StatusPedido.ENTREGUE);
            pedidoRepository.save(pedido);
        } else {
            throw new SolicitacaoNegadaException("Pedido já cancelado.");
        }

        return pedido;
    }

    public Pedido createPedido(Pedido pedido, int idUsuarioAuth) {
        if (pedido.getIdUsuario() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Pedido não pertence ao usuário.");
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void cancelarPedido(int id, int idUsuarioAuth) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RegistroInexistenteException("Pedido não encontrado."));

        if (pedido.getIdUsuario() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Pedido não pertence ao usuário.");
        }

        // Cancelar itens e devolver estoque
        List<ItemPedido> itensPedido = itemPedidoRepository.findAllByIdPedido(id);

        if (!itensPedido.isEmpty()) {
            for (ItemPedido itemPedido : itensPedido) {
                itemPedido.setStatus(StatusItemPedido.CANCELADO);

                Produto produto = produtoRepository.findById(itemPedido.getIdProduto())
                        .orElseThrow(() -> new RegistroInexistenteException("Produto não encontrado."));

                // Retorna a quantidade reservada para o estoque
                produto.setQuantidade(produto.getQuantidade() + itemPedido.getQuantidade());
                produtoRepository.save(produto);
            }
            itemPedidoRepository.saveAll(itensPedido);
        }

        // Tratar o pagamento
        pagamentoRepository.findByIdPedido(id).ifPresent(pagamento -> {
            if (pagamento.getStatus() == StatusPagamento.APROVADO) {
                pagamento.setStatus(StatusPagamento.ESTORNADO);
            } else {
                pagamento.setStatus(StatusPagamento.CANCELADO);
            }
            pagamentoRepository.save(pagamento);
        });

        // Atualizar status do pedido
        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }
}
