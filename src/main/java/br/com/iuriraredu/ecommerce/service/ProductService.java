package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.ProductRequestDTO;
import br.com.iuriraredu.ecommerce.dto.ProductResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Product;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.mapper.ProductMapper;
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
    private final ProductMapper productMapper;

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO create(final ProductRequestDTO dto) {
        final Product product = productMapper.toEntity(dto);
        return productMapper.toResponseDTO(productRepository.save(product));
    }

    @Cacheable(value = "products")
    public List<ProductResponseDTO> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO findById(final Long id) {
        return productMapper.toResponseDTO(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO update(final Long id, final ProductRequestDTO dto) {
        final Product product = findEntityById(id);
        productMapper.updateEntityFromDto(dto, product);
        return productMapper.toResponseDTO(productRepository.save(product));
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void delete(final Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // Internal use only: no other class calls this method, so there's no reason for it to be
    // more visible than it needs to be (encapsulation).
    private Product findEntityById(final Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
