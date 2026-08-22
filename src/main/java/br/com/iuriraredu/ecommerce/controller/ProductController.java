package br.com.iuriraredu.ecommerce.controller;

import br.com.iuriraredu.ecommerce.dto.ProductRequestDTO;
import br.com.iuriraredu.ecommerce.dto.ProductResponseDTO;
import br.com.iuriraredu.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Catalog product management (Create, Read, Update and Delete)")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(
            summary = "Register a new product",
            description = "Creates a new product in stock using the data provided in the request body and returns the newly created product with its generated ID."
    )
    @ApiResponse(responseCode = "201", description = "Product registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data provided for product registration")
    @ApiResponse(responseCode = "406", description = "Incorrect 'Accept' header")
    @ApiResponse(responseCode = "415", description = "Incorrect 'Content-Type' header")
    public ResponseEntity<ProductResponseDTO> create(@RequestBody @Valid final ProductRequestDTO dto) {
        ProductResponseDTO created = productService.create(dto);
        return ResponseEntity.status(CREATED).body(created);
    }

    @GetMapping
    @Operation(
            summary = "List all products",
            description = "Returns a list containing all products currently registered in the system."
    )
    @ApiResponse(responseCode = "200", description = "List of products returned successfully")
    public List<ProductResponseDTO> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Find product by ID",
            description = "Returns the details of a specific product based on the ID provided in the URL."
    )
    @ApiResponse(responseCode = "200", description = "Product found successfully")
    @ApiResponse(responseCode = "404", description = "Product not found for the given ID")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable final Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update product by ID",
            description = "Updates the details of a specific product based on the ID provided in the URL and returns the updated product."
    )
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data provided for the update")
    @ApiResponse(responseCode = "404", description = "Product not found for the given ID")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable final Long id, @RequestBody @Valid final ProductRequestDTO dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete product",
            description = "Removes a product from the system based on the ID provided in the URL. Returns no content on success."
    )
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found for the given ID")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
