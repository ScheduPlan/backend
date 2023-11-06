package de.hofuniversity.assemblyplanner.persistence.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import jakarta.persistence.*;

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
    @ManyToMany private List<Part> parts;

    public Product(Description description, double materialWidth, String materialName, String materialGroup, String productGroup, List<Part> parts) {
        this.description = description;
        this.materialWidth = materialWidth;
        this.materialName = materialName;
        this.materialGroup = materialGroup;
        this.productGroup = productGroup;
        this.parts = parts;
    }

    public Product() {

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

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
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
