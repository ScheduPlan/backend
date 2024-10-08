package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.Part;
import de.hofuniversity.assemblyplanner.persistence.model.Product;
import de.hofuniversity.assemblyplanner.persistence.model.ProductPart;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductPartAppendRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductPartUpdateRequest;

import java.util.UUID;

public interface ProductPartService {
    Iterable<Part> getParts(UUID productId);

    Part getPart(UUID productId, UUID partId);

    Product addPart(UUID productId, ProductPartAppendRequest appendRequest);

    ProductPart deletePart(UUID productId, UUID partId);

    ProductPart updateAmount(UUID productId,
                             UUID partId,
                             ProductPartUpdateRequest updateRequest);
}
