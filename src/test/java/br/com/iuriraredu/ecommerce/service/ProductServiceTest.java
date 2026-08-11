package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.Product;
import br.com.iuriraredu.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should create product successfully")
    void createProductSuccess() {
        // Arrange
        Product product = new Product();
        product.setName("Vinyl Record");
        product.setPrice(BigDecimal.valueOf(120.00));

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product createdProduct = productService.create(product);

        // Assert
        assertNotNull(createdProduct);
        assertEquals("Vinyl Record", createdProduct.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Should return all products")
    void getAllProductsSuccess() {
        // Arrange
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAll();

        // Assert
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find product by id when product exists")
    void findByIdSuccess() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        Optional<Product> result = productService.findById(productId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(productId, result.get().getId());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should update product when product exists")
    void updateProductSuccess() {
        // Arrange
        Long productId = 1L;
        Product updatedData = new Product();
        updatedData.setName("Updated Product Name");

        when(productRepository.existsById(productId)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(updatedData);

        // Act
        Optional<Product> result = productService.update(productId, updatedData);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Updated Product Name", result.get().getName());
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).save(updatedData);
    }

    @Test
    @DisplayName("Should return empty when trying to update non-existent product")
    void updateProductNotFound() {
        // Arrange
        Long productId = 99L;
        Product updatedData = new Product();

        when(productRepository.existsById(productId)).thenReturn(false);

        // Act
        Optional<Product> result = productService.update(productId, updatedData);

        // Assert
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete product when product exists")
    void deleteProductSuccess() {
        // Arrange
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);

        // Act
        boolean result = productService.delete(productId);

        // Assert
        assertTrue(result);
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("Should return false when trying to delete non-existent product")
    void deleteProductNotFound() {
        // Arrange
        Long productId = 99L;
        when(productRepository.existsById(productId)).thenReturn(false);

        // Act
        boolean result = productService.delete(productId);

        // Assert
        assertFalse(result);
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, never()).deleteById(any());
    }
}