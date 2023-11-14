package de.hofuniversity.assemblyplanner.controller;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Part;
import de.hofuniversity.assemblyplanner.persistence.model.Product;
import de.hofuniversity.assemblyplanner.persistence.model.ProductPart;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductPartAppendRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductPartUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.PartRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products/{productId}/parts")
public class ProductPartController {

    private final PartRepository partRepository;
    private final ProductRepository productRepository;

    public ProductPartController(@Autowired PartRepository partRepository,
                                 @Autowired ProductRepository productRepository) {
        this.partRepository = partRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    @Operation(summary = "gets all parts associated to a product")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Part> getParts(@PathVariable UUID productId) {
        return partRepository.findPartsByProductId(productId);
    }

    @GetMapping("/{partId}")
    @Operation(summary = "gets a certain part associated to a product", responses = {
            @ApiResponse(responseCode = "404", description = "the requested part is not associated to the requested product, " +
                    "or the requested product was not found.")
    })
    @ResponseStatus(HttpStatus.OK)
    public Part getPart(@PathVariable UUID productId, @PathVariable UUID partId) {
        return partRepository
                .findPartByProductId(productId, partId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping
    @Operation(summary = "adds a certain part to the given product with the specified amount of parts", responses = {
            @ApiResponse(responseCode = "404", description = "the requested part or product was not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Product addPart(@PathVariable UUID productId, @RequestBody ProductPartAppendRequest appendRequest) {
        Product product = productRepository.findById(productId).orElseThrow(ResourceNotFoundException::new);
        Part part = partRepository.findById(appendRequest.partId()).orElseThrow(ResourceNotFoundException::new);
        product.addPart(part, appendRequest.amount());
        partRepository.save(part);
        return productRepository.save(product);
    }

    @DeleteMapping("/{partId}")
    @Operation(summary = "removes the specified part from the requested product", responses = {
            @ApiResponse(responseCode = "404", description = "the requested part or product was not found, " +
                    "or the part is not associated to the product")
    })
    @ResponseStatus(HttpStatus.OK)
    public ProductPart deletePart(@PathVariable UUID productId, @PathVariable UUID partId) {
        Product product = productRepository.findById(productId).orElseThrow(ResourceNotFoundException::new);
        Part part = partRepository.findPartByProductId(productId, partId).orElseThrow(ResourceNotFoundException::new);
        ProductPart association = product.removePart(part);
        partRepository.save(part);
        productRepository.save(product);
        return association;
    }

    @PutMapping("/{partId}")
    @Operation(summary = "updates a product-part association to use the given amount", responses = {
            @ApiResponse(responseCode = "404", description = "the given part or product was not found, or no association " +
                    "exists between the two.")
    })
    @ResponseStatus(HttpStatus.OK)
    public ProductPart updateAmount(@PathVariable UUID productId,
                                    @PathVariable UUID partId,
                                    @RequestBody ProductPartUpdateRequest updateRequest) {
        Part part = partRepository.findPartByProductId(productId, partId).orElseThrow(ResourceNotFoundException::new);
        ProductPart productPart = part.getProducts().stream()
                .filter(pp -> pp.getProduct().getId().equals(productId))
                .findAny()
                .orElseThrow(ResourceNotFoundException::new);

        productPart.setAmount(updateRequest.amount());
        partRepository.save(part);
        productRepository.save(productPart.getProduct());
        return productPart;
    }
}
