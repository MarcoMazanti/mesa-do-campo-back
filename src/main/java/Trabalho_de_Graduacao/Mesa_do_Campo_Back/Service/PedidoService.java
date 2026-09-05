package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.CheckoutRequestDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.ItemCheckoutDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.PedidoDetalhadoDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPagamento;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pagamento;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public Pedido getById(int id, int idUsuarioAuth) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RegistroInexistenteException("Pedido não encontrado."));

        if (pedido.getIdUsuario() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Este pedido não pertence ao usuário.");
        }

        return pedido;
    }

    // Visão completa do pedido: cabeçalho + itens + pagamento (se já existir).
    public PedidoDetalhadoDTO getDetalhe(int id, int idUsuarioAuth) {
        Pedido pedido = getById(id, idUsuarioAuth);

        List<ItemPedido> itens = itemPedidoRepository.findAllByIdPedido(id);
        Pagamento pagamento = pagamentoRepository.findByIdPedido(id).orElse(null);

        return new PedidoDetalhadoDTO(pedido, itens, pagamento);
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

    /*
     * Avança manualmente o status do pedido entre AGUARDANDO_PAGAMENTO,
     * PROCESSANDO e ENVIADO. ENTREGUE e CANCELADO continuam tendo seus
     * próprios endpoints dedicados (com regras próprias de estoque/pagamento).
     */
    public Pedido atualizarStatus(int id, StatusPedido novoStatus, int idUsuarioAuth) {
        if (novoStatus == StatusPedido.ENTREGUE || novoStatus == StatusPedido.CANCELADO) {
            throw new SolicitacaoNegadaException(
                    "Use os endpoints /pedidos/entregue/{id} ou /pedidos/cancel/{id} para este status.");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RegistroInexistenteException("Pedido não encontrado."));

        if (pedido.getIdUsuario() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Este pedido não pertence ao usuário.");
        }

        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new SolicitacaoNegadaException("Não é possível alterar um pedido " + pedido.getStatus() + ".");
        }

        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    public Pedido createPedido(Pedido pedido, int idUsuarioAuth) {
        if (pedido.getIdUsuario() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Pedido não pertence ao usuário.");
        }
        return pedidoRepository.save(pedido);
    }

    /*
     * Fluxo completo e atômico de finalização de compra: recebe os itens do
     * carrinho, valida estoque, cria o Pedido, os ItemPedido de cada produto
     * (com o preço travado no valor atual do produto — nunca confiando em um
     * preço vindo do cliente) e o Pagamento (simulado como aprovado, já que
     * não existe integração com um gateway real neste projeto).
     */
    @Transactional
    public PedidoDetalhadoDTO checkout(CheckoutRequestDTO checkout, int idUsuarioAuth) {
        if (checkout.getItens() == null || checkout.getItens().isEmpty()) {
            throw new SolicitacaoNegadaException("O pedido precisa ter ao menos um item.");
        }

        // Valida estoque e calcula o total a partir do preço ATUAL de cada produto
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCheckoutDTO itemDTO : checkout.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getIdProduto())
                    .orElseThrow(() -> new RegistroInexistenteException("Produto não encontrado: " + itemDTO.getIdProduto()));

            if (produto.getQuantidade() < itemDTO.getQuantidade()) {
                throw new SolicitacaoNegadaException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            total = total.add(produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));
        }

        // Cria o "cabeçalho" do pedido
        Pedido pedido = pedidoRepository.save(new Pedido(idUsuarioAuth, total));

        // Cria cada item, já descontando o estoque
        for (ItemCheckoutDTO itemDTO : checkout.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getIdProduto())
                    .orElseThrow(() -> new RegistroInexistenteException("Produto não encontrado: " + itemDTO.getIdProduto()));

            ItemPedido item = new ItemPedido(pedido.getId(), produto.getId(), itemDTO.getQuantidade(), produto.getPreco());
            itemPedidoRepository.save(item);

            produto.setQuantidade(produto.getQuantidade() - itemDTO.getQuantidade());
            produtoRepository.save(produto);
        }

        // Registra o pagamento (simulado como já aprovado) e avança o pedido
        Pagamento pagamento = pagamentoRepository.save(new Pagamento(
                pedido.getId(),
                checkout.getMetodoPagamento(),
                StatusPagamento.APROVADO,
                LocalDateTime.now(),
                total
        ));

        pedido.setStatus(StatusPedido.PROCESSANDO);
        pedidoRepository.save(pedido);

        List<ItemPedido> itens = itemPedidoRepository.findAllByIdPedido(pedido.getId());

        return new PedidoDetalhadoDTO(pedido, itens, pagamento);
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
