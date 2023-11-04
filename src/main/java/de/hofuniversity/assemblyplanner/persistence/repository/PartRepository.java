package de.hofuniversity.assemblyplanner.persistence.repository;

import de.hofuniversity.assemblyplanner.persistence.model.Part;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PartRepository extends CrudRepository<Part, UUID> {
}
