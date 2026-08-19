package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.ProductRequestDTO;
import br.com.iuriraredu.ecommerce.dto.ProductResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Product;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO create(ProductRequestDTO dto) {
        Product product = toEntity(dto);
        return ProductResponseDTO.fromEntity(productRepository.save(product));
    }

    @Cacheable(value = "products")
    public List<ProductResponseDTO> getAll() {
        return productRepository.findAll().stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO findById(Long id) {
        return ProductResponseDTO.fromEntity(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product product = findEntityById(id);
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStockQuantity(dto.stockQuantity());
        product.setActive(dto.active());
        return ProductResponseDTO.fromEntity(productRepository.save(product));
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // Uso interno (ex.: OrderService) quando é preciso a entidade gerenciada, não o DTO.
    Product findEntityById(Long id) { // cadê o "private" se é pra uso interno?
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStockQuantity(dto.stockQuantity());
        product.setActive(dto.active() != null ? dto.active() : Boolean.TRUE);
        return product;
    }
}