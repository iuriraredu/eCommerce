package br.com.iuriraredu.ecommerce.service;


import br.com.iuriraredu.ecommerce.entity.Cliente;
import br.com.iuriraredu.ecommerce.entity.Endereco;
import br.com.iuriraredu.ecommerce.entity.ItemPedido;
import br.com.iuriraredu.ecommerce.entity.Pedido;
import br.com.iuriraredu.ecommerce.entity.Produto;
import br.com.iuriraredu.ecommerce.entity.emuns.StatusPedido;
import br.com.iuriraredu.ecommerce.repository.ClienteRepository;
import br.com.iuriraredu.ecommerce.repository.PedidoRepository;
import br.com.iuriraredu.ecommerce.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;

    public Pedido criar(Pedido pedido) {

        // 1. BUSCAR O CLIENTE
        Cliente cliente = clienteRepository.findById(pedido.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado!"));

        pedido.setCliente(cliente);
        pedido.setDocumentoClienteSnapshot(cliente.getCpf());

        // 2. LÓGICA DE SNAPSHOT DO ENDEREÇO
        if (cliente.getEnderecos() != null && !cliente.getEnderecos().isEmpty()) {

            // Tenta encontrar o endereço pelo ID enviado no JSON. Se não vier ID, pega o primeiro da lista.
            Endereco enderecoEscolhido = cliente.getEnderecos().stream()
                    .filter(end -> end.getId().equals(pedido.getIdEnderecoEntrega()))
                    .findFirst()
                    .orElse(cliente.getEnderecos().getFirst());

            // Monta o texto do Snapshot (ex: "Rua X, 123 - Centro, CEP: 00000-000")
            String snapshot = String.format(
                    "%s, %s - %s, CEP: %s%s",
                    enderecoEscolhido.getLogradouro(),
                    enderecoEscolhido.getNumero(),
                    enderecoEscolhido.getBairro(),
                    enderecoEscolhido.getCep(),
                    enderecoEscolhido.getComplemento() != null
                            ? String.format(" (%s)", enderecoEscolhido.getComplemento())
                            : ""
            );

            pedido.setEnderecoEntregaSnapshot(snapshot);
        } else {
            pedido.setEnderecoEntregaSnapshot("Cliente sem endereço cadastrado");
        }

        // 3. PROCESSAR OS ITENS E CONGELAR O PREÇO
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

            item.setPrecoVendido(produto.getPreco());
            item.setProduto(produto);
            item.setPedido(pedido);
        }

        // 4. SALVAR TUDO
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> atualizarStatus(Long id, StatusPedido novoStatus) {
// 1. Busca o pedido existente no banco
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);

        // 2. Se não achar, retorna vazio
        if (pedidoOpt.isEmpty()) {
            return Optional.empty();
        }

        // 3. Se achar, pega o pedido, atualiza o status e salva
        Pedido pedido = pedidoOpt.get();
        pedido.setStatus(novoStatus);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return Optional.of(pedidoSalvo);
    }

}
