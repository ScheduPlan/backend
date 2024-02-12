package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Role;

public record EmployeeQuery (String firstName, String lastName, Boolean unassigned, Role role) {
}
