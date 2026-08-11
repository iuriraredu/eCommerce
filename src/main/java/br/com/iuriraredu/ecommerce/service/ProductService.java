package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.Product;
import br.com.iuriraredu.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> update(Long id, Product productUpdated) {
        if (!productRepository.existsById(id)) return Optional.empty();
        productUpdated.setId(id);
        return Optional.of(productRepository.save(productUpdated));
    }

    public boolean delete(Long id) {
        if (!productRepository.existsById(id)) return false;
        productRepository.deleteById(id);
        return true;
    }
}
