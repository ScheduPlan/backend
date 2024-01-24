package de.hofuniversity.assemblyplanner.persistence.model.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TeamDescription {
    @Column(unique = true)
    private String name;
    @Column()
    private String description;

    public TeamDescription(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public TeamDescription() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
