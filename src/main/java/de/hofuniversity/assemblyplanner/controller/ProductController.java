package de.hofuniversity.assemblyplanner.controller;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Product;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@Tag(name = "Products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(@Autowired ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    @Operation(summary = "retrieves all existing products")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Product> getProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{productId}")
    @Operation(summary = "retrieves a single product", responses = {
            @ApiResponse(responseCode = "404", description = "The product identified by this ID does not exist")
    })
    @ResponseStatus(HttpStatus.OK)
    public Product getProduct(@PathVariable UUID productId) {
        return productRepository
                .findById(productId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping
    @Operation(summary = "creates a new product")
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody ProductCreateRequest createRequest) {
        Product product = new Product(
                new Description(createRequest.name(), createRequest.description()),
                createRequest.materialWidth(),
                createRequest.materialName(),
                createRequest.materialGroup(),
                createRequest.productGroup()
        );

        return productRepository.save(product);
    }

    @PatchMapping("/{productId}")
    @Operation(summary = "patches product fields", responses = {
            @ApiResponse(responseCode = "404", description = "the product was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Product patchProduct(@PathVariable UUID productId, @RequestBody ProductUpdateRequest updateRequest) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(ResourceNotFoundException::new);

        if(updateRequest.materialGroup() != null)
            product.setMaterialGroup(updateRequest.productGroup());
        if(updateRequest.productGroup() != null)
            product.setProductGroup(updateRequest.productGroup());
        if(updateRequest.materialWidth() != 0.0)
            product.setMaterialWidth(updateRequest.materialWidth());
        if(updateRequest.description() != null)
            product.getDescription().setDescription(updateRequest.description());
        if(updateRequest.name() != null)
            product.getDescription().setName(updateRequest.name());

        return productRepository.save(product);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "updates product fields", responses = {
            @ApiResponse(responseCode = "404", description = "the product was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Product putProduct(@PathVariable UUID productId, @RequestBody ProductUpdateRequest updateRequest) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(ResourceNotFoundException::new);

        BeanUtils.copyProperties(updateRequest, product, "description");
        product.getDescription().setName(updateRequest.name());
        product.getDescription().setDescription(updateRequest.description());

        return productRepository.save(product);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "deletes a product", responses = {
            @ApiResponse(responseCode = "404", description = "the product was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Product deleteProduct(@PathVariable UUID productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(ResourceNotFoundException::new);

        productRepository.delete(product);
        return product;
    }
}
