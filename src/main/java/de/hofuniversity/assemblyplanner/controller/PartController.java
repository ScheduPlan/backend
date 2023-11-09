package de.hofuniversity.assemblyplanner.controller;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Part;
import de.hofuniversity.assemblyplanner.persistence.model.dto.PartCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.repository.PartRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/parts")
public class PartController {

    private PartRepository partRepository;

    public PartController(@Autowired PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @GetMapping
    @Operation(summary = "gets all existing parts")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Part> getParts(@PathVariable UUID productId) {
        return partRepository.findAll();
    }

    @GetMapping("/{partId}")
    @Operation(summary = "gets a single existing part", responses = {
            @ApiResponse(responseCode = "404", description = "the part wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Part getPart(@PathVariable UUID partId) {
        return partRepository.findById(partId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping
    @Operation(summary = "creates a part")
    @ResponseStatus(HttpStatus.CREATED)
    public Part createPart(@RequestBody PartCreateRequest partCreateRequest) {
        Part part = new Part(
                new Description(partCreateRequest.name(),
                        partCreateRequest.description()
                ),
                null
        );

        return partRepository.save(part);
    }

    @PatchMapping("/{partId}")
    @Operation(summary = "patches a single existing part. NULL values are ignored.", responses = {
            @ApiResponse(responseCode = "404", description = "the part wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Part patchPart(@PathVariable UUID partId, @RequestBody PartCreateRequest partCreateRequest) {
        Part part = partRepository.findById(partId)
                .orElseThrow(ResourceNotFoundException::new);

        if(partCreateRequest.name() != null)
            part.getDescription().setName(partCreateRequest.name());
        if(partCreateRequest.description() != null)
            part.getDescription().setDescription(partCreateRequest.description());

        return partRepository.save(part);
    }

    @PutMapping("/{partId}")
    @Operation(summary = "updates a single existing part", responses = {
            @ApiResponse(responseCode = "404", description = "the part wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Part putPart(@PathVariable UUID partId, @RequestBody PartCreateRequest partCreateRequest) {
        Part part = partRepository.findById(partId)
                .orElseThrow(ResourceNotFoundException::new);

        BeanUtils.copyProperties(partCreateRequest, part);

        return partRepository.save(part);
    }

    @DeleteMapping("/{partId}")
    @Operation(summary = "deletes a single existing part", responses = {
            @ApiResponse(responseCode = "404", description = "the part wasn't found")
    })
    @ResponseStatus(HttpStatus.OK)
    public Part deletePart(@PathVariable UUID partId) {
        Part part = partRepository.findById(partId)
                .orElseThrow(ResourceNotFoundException::new);

        partRepository.delete(part);
        return part;
    }
}
