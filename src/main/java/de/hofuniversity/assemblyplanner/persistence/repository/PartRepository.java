package de.hofuniversity.assemblyplanner.persistence.repository;

import de.hofuniversity.assemblyplanner.persistence.model.Part;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PartRepository extends CrudRepository<Part, UUID> {

    @Query("SELECT p.part FROM ProductPart p WHERE p.key.productId = :productId")
    Set<Part> findPartsByProductId(UUID productId);

    @Query("SELECT p.part FROM ProductPart p WHERE p.key.productId = :productId AND p.key.partId = :partId")
    Optional<Part> findPartByProductId(UUID productId, UUID partId);
}
