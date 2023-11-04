package de.hofuniversity.assemblyplanner.persistence.model;

import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class AssemblyTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Embedded private Description description;
    @OneToMany private List<Employee> employees;
}
