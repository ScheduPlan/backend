package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.Part;
import de.hofuniversity.assemblyplanner.persistence.model.dto.DescribableResourceRequest;
import de.hofuniversity.assemblyplanner.service.api.PartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/parts")
public class PartController {

    private final PartService partService;

    @Autowired
    public PartController(PartService partService) {
        this.partService = partService;
    }

    @GetMapping
    @Operation(summary = "gets all existing parts")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Part> getParts(@PathVariable UUID productId) {
        return partService.getParts(productId);
    }

    @GetMapping("/{partId}")
    @Operation(summary = "gets a single existing part", responses = {
            @ApiResponse(responseCode = "404", description = "the part wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Part getPart(@PathVariable UUID partId) {
        return partService.getPart(partId);
    }

    @PostMapping
    @Operation(summary = "creates a part")
    @ResponseStatus(HttpStatus.CREATED)
    public Part createPart(@RequestBody @Valid DescribableResourceRequest partCreateRequest) {
        return partService.createPart(partCreateRequest);
    }

    @PatchMapping("/{partId}")
    @Operation(summary = "patches a single existing part. NULL values are ignored.", responses = {
            @ApiResponse(responseCode = "404", description = "the part wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Part patchPart(@PathVariable UUID partId, @RequestBody DescribableResourceRequest partCreateRequest) {
        return partService.patchPart(partId, partCreateRequest);
    }

    @PutMapping("/{partId}")
    @Operation(summary = "updates a single existing part", responses = {
            @ApiResponse(responseCode = "404", description = "the part wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Part putPart(@PathVariable UUID partId, @RequestBody DescribableResourceRequest partCreateRequest) {
        return partService.putPart(partId, partCreateRequest);
    }

    @DeleteMapping("/{partId}")
    @Operation(summary = "deletes a single existing part", responses = {
            @ApiResponse(responseCode = "404", description = "the part wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Part deletePart(@PathVariable UUID partId) {
        return partService.deletePart(partId);
    }
}
