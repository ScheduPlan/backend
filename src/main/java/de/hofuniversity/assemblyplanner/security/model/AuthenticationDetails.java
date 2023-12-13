package de.hofuniversity.assemblyplanner.security.model;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;

public record AuthenticationDetails(Employee employee, String token, String refreshToken) {
}
