package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Part;
import de.hofuniversity.assemblyplanner.persistence.model.dto.DescribableResourceRequest;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.repository.PartRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Service
public class PartServiceImpl implements de.hofuniversity.assemblyplanner.service.api.PartService {

    private final PartRepository partRepository;

    public PartServiceImpl(@Autowired PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Override
    public Iterable<Part> getParts(UUID productId) {
        return partRepository.findAll();
    }

    @Override
    public Part getPart(UUID partId) {
        return partRepository.findById(partId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Part createPart(DescribableResourceRequest partCreateRequest) {
        Part part = new Part(
                new Description(partCreateRequest.name(),
                        partCreateRequest.description()
                ),
                null
        );

        return partRepository.save(part);
    }

    @Override
    public Part patchPart(UUID partId, DescribableResourceRequest partCreateRequest) {
        Part part = partRepository.findById(partId)
                .orElseThrow(ResourceNotFoundException::new);

        if(partCreateRequest.name() != null)
            part.getDescription().setName(partCreateRequest.name());
        if(partCreateRequest.description() != null)
            part.getDescription().setDescription(partCreateRequest.description());

        return partRepository.save(part);
    }

    @Override
    public Part putPart(UUID partId, DescribableResourceRequest partCreateRequest) {
        Part part = partRepository.findById(partId)
                .orElseThrow(ResourceNotFoundException::new);

        BeanUtils.copyProperties(partCreateRequest, part);

        return partRepository.save(part);
    }

    @Override
    public Part deletePart(UUID partId) {
        Part part = partRepository.findById(partId)
                .orElseThrow(ResourceNotFoundException::new);

        partRepository.delete(part);
        return part;
    }
}
