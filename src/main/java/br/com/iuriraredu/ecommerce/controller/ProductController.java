package br.com.iuriraredu.ecommerce.controller;

import br.com.iuriraredu.ecommerce.entity.Product;
import br.com.iuriraredu.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Gerenciamento de produtos do catálogo (Cadastro, Consulta, Atualização e Exclusão)")
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Cadastrar novo produto",
            description = "Cria um novo produto no estoque utilizando os dados fornecidos no corpo da requisição e retorna o produto recém-criado com seu ID gerado."
    )
    @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para o cadastro do produto")
    @ApiResponse(responseCode = "406", description = "'Accept' incorreto")
    @ApiResponse(responseCode = "415", description = "'Content-Type' incorreto")
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product createdProduct = productService.create(product);
        return ResponseEntity.status(CREATED).body(createdProduct);
    }

    @GetMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Listar todos os produtos",
            description = "Retorna uma lista contendo todos os produtos cadastrados atualmente no sistema."
    )
    @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    @ApiResponse(responseCode = "406", description = "'Accept' incorreto")
    @ApiResponse(responseCode = "415", description = "'Content-Type' incorreto")
    public List<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Buscar produto por ID",
            description = "Retorna os detalhes de um produto específico com base no ID informado na URL."
    )
    @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado para o ID informado")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PutMapping(value = "/{id}",consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Atualizar produto por ID",
            description = "Atualiza os detalhes de um produto específico com base no ID informado na URL e retorna o produto atualizado."
    )
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para a atualização")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado para o ID informado")
    @ApiResponse(responseCode = "406", description = "'Accept' incorreto")
    @ApiResponse(responseCode = "415", description = "'Content-Type' incorreto")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return ResponseEntity.ok(productService.update(id, updatedProduct));
    }

    @DeleteMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Deletar produto",
            description = "Remove um produto do sistema com base no ID informado na URL. Retorna status sem conteúdo em caso de sucesso."
    )
    @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado para o ID informado")
    @ApiResponse(responseCode = "406", description = "'Accept' incorreto")
    @ApiResponse(responseCode = "415", description = "'Content-Type' incorreto")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}