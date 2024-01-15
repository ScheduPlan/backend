package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.Part;
import de.hofuniversity.assemblyplanner.persistence.model.dto.DescribableResourceRequest;

import java.util.UUID;

public interface PartService {
    Iterable<Part> getParts(UUID productId);

    Part getPart(UUID partId);

    Part createPart(DescribableResourceRequest partCreateRequest);

    Part patchPart(UUID partId, DescribableResourceRequest partCreateRequest);

    Part putPart(UUID partId, DescribableResourceRequest partCreateRequest);

    Part deletePart(UUID partId);
}
