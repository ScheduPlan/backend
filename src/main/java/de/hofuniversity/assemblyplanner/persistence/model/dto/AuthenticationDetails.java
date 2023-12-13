package de.hofuniversity.assemblyplanner.persistence.model.dto;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;

public record AuthenticationDetails(Employee employee, String token, String refreshToken) {
}
