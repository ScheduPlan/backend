package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.Part;
import de.hofuniversity.assemblyplanner.persistence.model.dto.DescribableResourceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface PartService {
    Iterable<Part> getParts(UUID productId);

    Part getPart(UUID partId);

    Part createPart(DescribableResourceRequest partCreateRequest);

    Part patchPart(UUID partId, DescribableResourceRequest partCreateRequest);

    Part putPart(UUID partId, DescribableResourceRequest partCreateRequest);

    Part deletePart(UUID partId);
}
