package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Enum.StatusPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.ItemPedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Pedido;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Produto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ItemPedidoRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.PedidoRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemPedidoService {
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    // Acesso liberado tanto para o comprador do pedido quanto para o vendedor do produto do item.
    public ItemPedido getById(int id, int idUsuarioAuth) {
        ItemPedido item = itemPedidoRepository.findById(id)
                .orElseThrow(() -> new RegistroInexistenteException("Item de pedido não encontrado."));

        validarAcesso(item, idUsuarioAuth);

        return item;
    }

    public List<ItemPedido> getAllByPedido(int idPedido, int idUsuarioAuth) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RegistroInexistenteException("Pedido não encontrado."));

        if (pedido.getIdUsuario() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Este pedido não pertence ao usuário.");
        }

        List<ItemPedido> itens = itemPedidoRepository.findAllByIdPedido(idPedido);

        if (itens.isEmpty()) throw new RegistroInexistenteException("Este pedido não possui itens.");

        return itens;
    }

    // Itens vendidos por um vendedor, usado em "Meu Negócio" para acompanhar os pedidos recebidos.
    public List<ItemPedido> getAllByVendedor(int idVendedor, int idUsuarioAuth) {
        if (idVendedor != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Só é possível consultar os próprios itens vendidos.");
        }

        List<ItemPedido> itens = itemPedidoRepository.findAllByIdVendedor(idVendedor);

        if (itens.isEmpty()) throw new RegistroInexistenteException("Nenhum item vendido encontrado.");

        return itens;
    }

    /*
     * Cria um item de forma avulsa em um pedido já existente (fora do fluxo de checkout).
     * O preço é sempre travado a partir do valor atual do produto (nunca confiar no que o cliente envia).
     */
    @Transactional
    public ItemPedido createItem(ItemPedido item, int idUsuarioAuth) {
        Pedido pedido = pedidoRepository.findById(item.getIdPedido())
                .orElseThrow(() -> new RegistroInexistenteException("Pedido não encontrado."));

        if (pedido.getIdUsuario() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Este pedido não pertence ao usuário.");
        }

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new SolicitacaoNegadaException("Só é possível adicionar itens a um pedido aguardando pagamento.");
        }

        Produto produto = produtoRepository.findById(item.getIdProduto())
                .orElseThrow(() -> new RegistroInexistenteException("Produto não encontrado."));

        if (produto.getQuantidade() < item.getQuantidade()) {
            throw new SolicitacaoNegadaException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        item.setId(0);
        item.setPreco(produto.getPreco());
        item.setStatus(StatusItemPedido.PENDENTE);

        produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
        produtoRepository.save(produto);

        ItemPedido salvo = itemPedidoRepository.save(item);

        recalcularTotalPedido(pedido);

        return salvo;
    }

    @Transactional
    public ItemPedido updateQuantidade(int id, int novaQuantidade, int idUsuarioAuth) {
        if (novaQuantidade <= 0) throw new SolicitacaoNegadaException("A quantidade deve ser maior que zero.");

        ItemPedido item = itemPedidoRepository.findById(id)
                .orElseThrow(() -> new RegistroInexistenteException("Item de pedido não encontrado."));

        Pedido pedido = pedidoRepository.findById(item.getIdPedido())
                .orElseThrow(() -> new RegistroInexistenteException("Pedido não encontrado."));

        if (pedido.getIdUsuario() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Este pedido não pertence ao usuário.");
        }

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new SolicitacaoNegadaException("Só é possível alterar itens de um pedido aguardando pagamento.");
        }

        Produto produto = produtoRepository.findById(item.getIdProduto())
                .orElseThrow(() -> new RegistroInexistenteException("Produto não encontrado."));

        int diferenca = novaQuantidade - item.getQuantidade();

        if (diferenca > 0 && produto.getQuantidade() < diferenca) {
            throw new SolicitacaoNegadaException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        produto.setQuantidade(produto.getQuantidade() - diferenca);
        produtoRepository.save(produto);

        item.setQuantidade(novaQuantidade);
        ItemPedido salvo = itemPedidoRepository.save(item);

        recalcularTotalPedido(pedido);

        return salvo;
    }

    // Só quem vende o produto do item pode avançar o status dele (ex: PREPARANDO -> EM_TRANSITO).
    public ItemPedido atualizarStatus(int id, StatusItemPedido novoStatus, int idUsuarioAuth) {
        ItemPedido item = itemPedidoRepository.findById(id)
                .orElseThrow(() -> new RegistroInexistenteException("Item de pedido não encontrado."));

        Produto produto = produtoRepository.findById(item.getIdProduto())
                .orElseThrow(() -> new RegistroInexistenteException("Produto não encontrado."));

        if (produto.getIdVendedor() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Apenas o vendedor do produto pode atualizar o status do item.");
        }

        if (item.getStatus() == StatusItemPedido.CANCELADO || item.getStatus() == StatusItemPedido.ENTREGUE) {
            throw new SolicitacaoNegadaException("Não é possível alterar um item que já está " + item.getStatus() + ".");
        }

        item.setStatus(novoStatus);
        return itemPedidoRepository.save(item);
    }

    @Transactional
    public void deleteItem(int id, int idUsuarioAuth) {
        ItemPedido item = itemPedidoRepository.findById(id)
                .orElseThrow(() -> new RegistroInexistenteException("Item de pedido não encontrado."));

        Pedido pedido = pedidoRepository.findById(item.getIdPedido())
                .orElseThrow(() -> new RegistroInexistenteException("Pedido não encontrado."));

        if (pedido.getIdUsuario() != idUsuarioAuth) {
            throw new SolicitacaoNegadaException("Este pedido não pertence ao usuário.");
        }

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new SolicitacaoNegadaException("Só é possível remover itens de um pedido aguardando pagamento.");
        }

        Produto produto = produtoRepository.findById(item.getIdProduto())
                .orElseThrow(() -> new RegistroInexistenteException("Produto não encontrado."));

        // Devolve o estoque reservado por este item
        produto.setQuantidade(produto.getQuantidade() + item.getQuantidade());
        produtoRepository.save(produto);

        itemPedidoRepository.deleteById(id);

        recalcularTotalPedido(pedido);
    }

    // Mantém Pedido.precoTotal sempre coerente com a soma dos seus itens ainda válidos.
    private void recalcularTotalPedido(Pedido pedido) {
        List<ItemPedido> itens = itemPedidoRepository.findAllByIdPedido(pedido.getId());

        BigDecimal total = itens.stream()
                .filter(i -> i.getStatus() != StatusItemPedido.CANCELADO)
                .map(i -> i.getPreco().multiply(BigDecimal.valueOf(i.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setPrecoTotal(total);
        pedidoRepository.save(pedido);
    }

    private void validarAcesso(ItemPedido item, int idUsuarioAuth) {
        Pedido pedido = pedidoRepository.findById(item.getIdPedido())
                .orElseThrow(() -> new RegistroInexistenteException("Pedido não encontrado."));

        if (pedido.getIdUsuario() == idUsuarioAuth) return;

        Produto produto = produtoRepository.findById(item.getIdProduto())
                .orElseThrow(() -> new RegistroInexistenteException("Produto não encontrado."));

        if (produto.getIdVendedor() == idUsuarioAuth) return;

        throw new SolicitacaoNegadaException("Você não tem acesso a este item.");
    }
}
