package de.hofuniversity.assemblyplanner.persistence.model;

import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Embedded private Description description;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductPart> products;

    public Part(Description description, Set<ProductPart> products) {
        this.description = description;
        this.products = products;
    }

    public Part() {

    }

    public Set<ProductPart> getProducts() {
        return products;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Part part = (Part) o;
        return Objects.equals(id, part.id) && Objects.equals(description, part.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description);
    }

    @Override
    public String toString() {
        return "Part{" +
                "id=" + id +
                ", description=" + description +
                '}';
    }
}
