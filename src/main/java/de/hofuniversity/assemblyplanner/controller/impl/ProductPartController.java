package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Part;
import de.hofuniversity.assemblyplanner.persistence.model.Product;
import de.hofuniversity.assemblyplanner.persistence.model.ProductPart;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductPartAppendRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductPartUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.PartRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.ProductRepository;
import de.hofuniversity.assemblyplanner.service.api.ProductPartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products/{productId}/parts")
public class ProductPartController {

    private final ProductPartService productPartService;

    public ProductPartController(ProductPartService productPartService) {
        this.productPartService = productPartService;
    }

    @GetMapping
    @Operation(summary = "gets all parts associated to a product")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Part> getParts(@PathVariable UUID productId) {
        return productPartService.getParts(productId);
    }

    @GetMapping("/{partId}")
    @Operation(summary = "gets a certain part associated to a product", responses = {
            @ApiResponse(responseCode = "404", description = "the requested part is not associated to the requested product, " +
                    "or the requested product was not found.")
    })
    @ResponseStatus(HttpStatus.OK)
    public Part getPart(@PathVariable UUID productId, @PathVariable UUID partId) {
        return productPartService.getPart(productId, partId);
    }

    @PostMapping
    @Operation(summary = "adds a certain part to the given product with the specified amount of parts", responses = {
            @ApiResponse(responseCode = "404", description = "the requested part or product was not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Product addPart(@PathVariable UUID productId, @RequestBody @Valid ProductPartAppendRequest appendRequest) {
        return productPartService.addPart(productId, appendRequest);
    }

    @DeleteMapping("/{partId}")
    @Operation(summary = "removes the specified part from the requested product", responses = {
            @ApiResponse(responseCode = "404", description = "the requested part or product was not found, " +
                    "or the part is not associated to the product")
    })
    @ResponseStatus(HttpStatus.OK)
    public ProductPart deletePart(@PathVariable UUID productId, @PathVariable UUID partId) {
        return productPartService.deletePart(productId, partId);
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
        return productPartService.updateAmount(productId, partId, updateRequest);
    }
}
