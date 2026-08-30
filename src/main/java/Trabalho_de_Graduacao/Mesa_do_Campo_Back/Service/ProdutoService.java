package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Produto;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Vendedor;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ProdutoRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private VendedorRepository vendedorRepository;

    public Produto getById(int id) {
        Optional<Produto> produtoOptional = produtoRepository.findById(id);

        if (produtoOptional.isPresent()) {
            return produtoOptional.get();
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhum produto com o ID: " + id);
    }

    public List<Produto> getAllProdutosByIdVendedor(int idVendedor) {
        List<Produto> produtoList = produtoRepository.findAllByIdVendedor(idVendedor);

        if (produtoList.isEmpty()) throw new RegistroInexistenteException("Não foi encontrado nenhum produto para este vendedor.");

        return produtoList;
    }

    public List<Produto> getAllProdutos() {
        List<Produto> produtoList = produtoRepository.findAll();

        if (produtoList.isEmpty()) throw new RegistroInexistenteException("Não possui produtos cadastrados.");

        return produtoList;
    }

    public Produto createProduto(Produto produto, int idUsuarioAuth) {
        Optional<Vendedor> vendedorOptional = vendedorRepository.findByIdVendedor(produto.getIdVendedor());

        if (vendedorOptional.isEmpty()) throw new SolicitacaoNegadaException("Não possui nenhum vendedor com o ID: " + produto.getIdVendedor() + " cadastrado.");
        if (vendedorOptional.get().getIdVendedor() != idUsuarioAuth) throw new SolicitacaoNegadaException("Apenas o vendedor pode cadastrar produtos.");

        return produtoRepository.save(produto);
    }

    public Produto updateProduto(Produto produto, int idUsuarioAuth) {
        Optional<Produto> produtoOptional = produtoRepository.findById(produto.getId());

        if (produtoOptional.isPresent()) {
            Produto produtoBanco = produtoOptional.get();

            if (produtoBanco.getIdVendedor() != idUsuarioAuth) throw new SolicitacaoNegadaException("Apenas o vendedor pode alterar os produtos.");

            // Alteração apenas dos dados que podem ser alterados
            produtoBanco.setNome(produto.getNome());
            produtoBanco.setPreco(produto.getPreco());
            produtoBanco.setDescricao(produto.getDescricao());
            produtoBanco.setQuantidade(produto.getQuantidade());

            return produtoRepository.save(produtoBanco);
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhum produto com o ID: " + produto.getId() + "para atualizar.");
    }

    public void deleteProduto(int idAlvo, int idUsuarioAuth) {
        Optional<Produto> produtoOptional = produtoRepository.findById(idAlvo);

        if (produtoOptional.isPresent()) {
            Produto produtoBanco = produtoOptional.get();

            if (produtoBanco.getIdVendedor() != idUsuarioAuth) throw new SolicitacaoNegadaException("Apenas o vendedor pode excluir os produtos.");

            produtoRepository.deleteById(idAlvo);
            return;
        }

        throw new RegistroInexistenteException("Não foi encontrado nenhum produto com o ID: " + idAlvo + "para excluir.");
    }
}
