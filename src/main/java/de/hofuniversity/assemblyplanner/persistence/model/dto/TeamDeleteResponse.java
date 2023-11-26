package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;

import java.util.UUID;

public record TeamDeleteResponse(UUID id, String name, String description) {
    public TeamDeleteResponse(AssemblyTeam team) {
        this(team.getId(), team.getDescription().getName(), team.getDescription().getDescription());
    }
}
