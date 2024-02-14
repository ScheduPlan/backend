package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Part;
import de.hofuniversity.assemblyplanner.persistence.model.dto.DescribableResourceRequest;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.repository.PartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PartServiceImpl implements de.hofuniversity.assemblyplanner.service.api.PartService {

    private final PartRepository partRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(PartServiceImpl.class);

    public PartServiceImpl(@Autowired PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Override
    public Iterable<Part> getParts() {
        return partRepository.findAll();
    }

    @Override
    public Part getPart(UUID partId) {
        LOGGER.info("retrieving part {}", partId);
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

        LOGGER.info("creating part from create request {}", partCreateRequest);

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

        LOGGER.info("updating part {} using patch {}", partId, partCreateRequest);

        return partRepository.save(part);
    }

    @Override
    public Part putPart(UUID partId, DescribableResourceRequest partCreateRequest) {
        Part part = partRepository.findById(partId)
                .orElseThrow(ResourceNotFoundException::new);

        BeanUtils.copyProperties(partCreateRequest, part);

        LOGGER.info("updating part {} using update {}", partId, partCreateRequest);

        return partRepository.save(part);
    }

    @Override
    public Part deletePart(UUID partId) {
        LOGGER.info("deleting part {}", partId);
        Part part = partRepository.findById(partId)
                .orElseThrow(ResourceNotFoundException::new);

        partRepository.delete(part);
        LOGGER.info("deleted part {}", partId);
        return part;
    }
}
