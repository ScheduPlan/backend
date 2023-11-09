package de.hofuniversity.assemblyplanner.persistence.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

import java.util.Objects;

@Entity
public class ProductPart {
    @EmbeddedId
    private ProductPartKey key;
    @ManyToOne
    @MapsId("productId")
    private Product product;

    @ManyToOne
    @MapsId("partId")
    private Part part;

    private int amount;

    public ProductPart(Product product, Part part, int amount) {
        this.product = product;
        this.part = part;
        this.amount = amount;
    }

    public ProductPart() {

    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductPart that = (ProductPart) o;
        return Objects.equals(product, that.product) && Objects.equals(part, that.part);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, part);
    }
}
