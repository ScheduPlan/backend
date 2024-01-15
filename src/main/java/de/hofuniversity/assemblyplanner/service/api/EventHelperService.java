package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.dto.Helper;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ResourceRequest;

import java.util.Set;
import java.util.UUID;

public interface EventHelperService {
    Helper getHelper(
            UUID customerId,
            UUID orderId,
            UUID eventId,
            UUID helperId);

    Set<Helper> getHelpers(
            UUID customerId,
            UUID orderId,
            UUID eventId);

    Set<Helper> addHelper(
            UUID customerId,
            UUID orderId,
            UUID eventId,
            ResourceRequest request);

    Set<Helper> removeHelper(
            UUID customerId,
            UUID orderId,
            UUID eventId,
            UUID helperId);
}
