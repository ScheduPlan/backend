package de.hofuniversity.assemblyplanner.persistence.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProductPartKey implements Serializable {

    private UUID productId;
    private UUID partId;

    public ProductPartKey(UUID productId, UUID partId) {
        this.productId = productId;
        this.partId = partId;
    }

    public UUID getProductId() {
        return productId;
    }

    public UUID getPartId() {
        return partId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductPartKey that = (ProductPartKey) o;
        return Objects.equals(productId, that.productId) && Objects.equals(partId, that.partId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, partId);
    }
}
