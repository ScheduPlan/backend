package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Product;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.repository.ProductRepository;
import de.hofuniversity.assemblyplanner.service.api.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "retrieves all existing products")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{productId}")
    @Operation(summary = "retrieves a single product", responses = {
            @ApiResponse(responseCode = "404", description = "The product identified by this ID does not exist")
    })
    @ResponseStatus(HttpStatus.OK)
    public Product getProduct(@PathVariable UUID productId) {
        return productService.getProduct(productId);
    }

    @PostMapping
    @Operation(summary = "creates a new product")
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody @Valid ProductCreateRequest createRequest) {
        return productService.createProduct(createRequest);
    }

    @PatchMapping("/{productId}")
    @Operation(summary = "patches product fields", responses = {
            @ApiResponse(responseCode = "404", description = "the product was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Product patchProduct(@PathVariable UUID productId, @RequestBody ProductUpdateRequest updateRequest) {
        return productService.patchProduct(productId, updateRequest);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "updates product fields", responses = {
            @ApiResponse(responseCode = "404", description = "the product was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Product putProduct(@PathVariable UUID productId, @RequestBody ProductUpdateRequest updateRequest) {
        return productService.putProduct(productId, updateRequest);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "deletes a product", responses = {
            @ApiResponse(responseCode = "404", description = "the product was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Product deleteProduct(@PathVariable UUID productId) {
        return productService.deleteProduct(productId);
    }
}
