package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.ProductRequestDTO;
import br.com.iuriraredu.ecommerce.dto.ProductResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Product;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        ProductRequestDTO dto = new ProductRequestDTO("Vinyl Record", "A record", BigDecimal.valueOf(120.00), 10, true);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Vinyl Record");
        savedProduct.setPrice(BigDecimal.valueOf(120.00));

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        ProductResponseDTO createdProduct = productService.create(dto);

        // Assert
        assertNotNull(createdProduct);
        assertEquals("Vinyl Record", createdProduct.name());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should return all products")
    void getAllProductsSuccess() {
        // Arrange
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<ProductResponseDTO> result = productService.getAll();

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
        ProductResponseDTO result = productService.findById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.id());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product by id does not exist")
    void findByIdNotFound() {
        // Arrange
        Long productId = 99L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> productService.findById(productId)
        );

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should update product when product exists")
    void updateProductSuccess() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Name");

        ProductRequestDTO updatedData = new ProductRequestDTO("Updated Product Name", "desc", BigDecimal.valueOf(150.00), 5, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Act
        ProductResponseDTO result = productService.update(productId, updatedData);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Product Name", result.name());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to update non-existent product")
    void updateProductNotFound() {
        // Arrange
        Long productId = 99L;
        ProductRequestDTO updatedData = new ProductRequestDTO("Name", "desc", BigDecimal.TEN, 1, true);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.update(productId, updatedData)
        );

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete product when product exists")
    void deleteProductSuccess() {
        // Arrange
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);

        // Act & Assert
        assertDoesNotThrow(() -> productService.delete(productId));

        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to delete non-existent product")
    void deleteProductNotFound() {
        // Arrange
        Long productId = 99L;
        when(productRepository.existsById(productId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.delete(productId)
        );

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, never()).deleteById(any());
    }
}