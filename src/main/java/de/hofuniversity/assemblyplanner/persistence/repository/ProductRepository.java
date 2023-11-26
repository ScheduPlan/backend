package de.hofuniversity.assemblyplanner.persistence.repository;

import de.hofuniversity.assemblyplanner.persistence.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface ProductRepository extends CrudRepository<Product, UUID> {

    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    Set<Product> findProductsByIds(Iterable<UUID> ids);
}
