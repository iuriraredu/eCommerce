package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.dto.ProductRequestDTO;
import br.com.iuriraredu.ecommerce.dto.ProductResponseDTO;
import br.com.iuriraredu.ecommerce.entity.Product;
import br.com.iuriraredu.ecommerce.exception.ResourceNotFoundException;
import br.com.iuriraredu.ecommerce.mapper.ProductMapper;
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

    // ProductService now delegates all DTO <-> entity conversion to ProductMapper (MapStruct).
    // Since the real generated implementation isn't available in a plain unit test, we mock the
    // interface and stub each conversion explicitly — the test still verifies ProductService's
    // own logic (what it calls, in what order), just not the mapping logic itself, which belongs
    // to ProductMapper and would be tested separately if it had any custom logic beyond field copying.
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should create product successfully")
    void createProductSuccess() {
        // Arrange
        final ProductRequestDTO dto = new ProductRequestDTO("Vinyl Record", "A record", BigDecimal.valueOf(120.00), 10, true);

        final Product mappedProduct = new Product();
        mappedProduct.setName("Vinyl Record");

        final Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Vinyl Record");
        savedProduct.setPrice(BigDecimal.valueOf(120.00));

        final ProductResponseDTO expectedResponse = new ProductResponseDTO(1L, "Vinyl Record", "A record", BigDecimal.valueOf(120.00), 10, true);

        when(productMapper.toEntity(dto)).thenReturn(mappedProduct);
        when(productRepository.save(mappedProduct)).thenReturn(savedProduct);
        when(productMapper.toResponseDTO(savedProduct)).thenReturn(expectedResponse);

        // Act
        final ProductResponseDTO createdProduct = productService.create(dto);

        // Assert
        assertNotNull(createdProduct);
        assertEquals("Vinyl Record", createdProduct.name());
        verify(productMapper, times(1)).toEntity(dto);
        verify(productRepository, times(1)).save(mappedProduct);
        verify(productMapper, times(1)).toResponseDTO(savedProduct);
    }

    @Test
    @DisplayName("Should return all products")
    void getAllProductsSuccess() {
        // Arrange
        final Product product1 = new Product();
        final Product product2 = new Product();
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));
        when(productMapper.toResponseDTO(any(Product.class)))
                .thenReturn(new ProductResponseDTO(1L, "A", null, BigDecimal.ONE, 1, true));

        // Act
        final List<ProductResponseDTO> result = productService.getAll();

        // Assert
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find product by id when product exists")
    void findByIdSuccess() {
        // Arrange
        final Long productId = 1L;
        final Product product = new Product();
        product.setId(productId);
        final ProductResponseDTO expectedResponse = new ProductResponseDTO(productId, "Name", null, BigDecimal.TEN, 5, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(expectedResponse);

        // Act
        final ProductResponseDTO result = productService.findById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.id());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product by id does not exist")
    void findByIdNotFound() {
        // Arrange
        final Long productId = 99L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> productService.findById(productId)
        );

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should update product when product exists")
    void updateProductSuccess() {
        // Arrange
        final Long productId = 1L;
        final Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Name");

        final ProductRequestDTO updatedData = new ProductRequestDTO("Updated Product Name", "desc", BigDecimal.valueOf(150.00), 5, true);
        final ProductResponseDTO expectedResponse = new ProductResponseDTO(productId, "Updated Product Name", "desc", BigDecimal.valueOf(150.00), 5, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);
        when(productMapper.toResponseDTO(existingProduct)).thenReturn(expectedResponse);

        // Act
        final ProductResponseDTO result = productService.update(productId, updatedData);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Product Name", result.name());
        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, times(1)).updateEntityFromDto(updatedData, existingProduct);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to update non-existent product")
    void updateProductNotFound() {
        // Arrange
        final Long productId = 99L;
        final ProductRequestDTO updatedData = new ProductRequestDTO("Name", "desc", BigDecimal.TEN, 1, true);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        final ResourceNotFoundException exception = assertThrows(
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
        final Long productId = 1L;
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
        final Long productId = 99L;
        when(productRepository.existsById(productId)).thenReturn(false);

        // Act & Assert
        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.delete(productId)
        );

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, never()).deleteById(any());
    }
}
