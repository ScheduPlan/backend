package de.hofuniversity.assemblyplanner.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Embedded @JsonUnwrapped private Description description;
    private double materialWidth;
    private String materialName;
    private String materialGroup;
    private String productGroup;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductPart> parts;

    public Product(Description description, double materialWidth, String materialName, String materialGroup, String productGroup) {
        this.description = description;
        this.materialWidth = materialWidth;
        this.materialName = materialName;
        this.materialGroup = materialGroup;
        this.productGroup = productGroup;
        this.parts = new ArrayList<>();
    }

    public Product() {

    }

    public ProductPart addPart(Part part, int amount) {
        ProductPart productPart = new ProductPart(this, part, amount);
        parts.add(productPart);
        part.getProducts().add(productPart);
        return productPart;
    }

    public ProductPart removePart(Part part) {
        ProductPart productPart = parts
                .stream()
                .filter(p -> p.getPart().equals(part))
                .findFirst()
                .orElse(null);

        if(productPart == null)
            return null;

        productPart.getPart().getProducts().remove(productPart);
        return productPart;
    }

    public UUID getId() {
        return id;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public double getMaterialWidth() {
        return materialWidth;
    }

    public void setMaterialWidth(double materialWidth) {
        this.materialWidth = materialWidth;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialGroup() {
        return materialGroup;
    }

    public void setMaterialGroup(String materialGroup) {
        this.materialGroup = materialGroup;
    }

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public List<ProductPart> getParts() {
        return parts;
    }

    public void setParts(List<ProductPart> parts) {
        this.parts = parts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(materialWidth, product.materialWidth) == 0 && Objects.equals(id, product.id) && Objects.equals(description, product.description) && Objects.equals(materialName, product.materialName) && Objects.equals(materialGroup, product.materialGroup) && Objects.equals(productGroup, product.productGroup) && Objects.equals(parts, product.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, materialWidth, materialName, materialGroup, productGroup, parts);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", description=" + description +
                ", materialWidth=" + materialWidth +
                ", materialName='" + materialName + '\'' +
                ", materialGroup='" + materialGroup + '\'' +
                ", productGroup='" + productGroup + '\'' +
                ", parts=" + parts +
                '}';
    }
}
