package de.hofuniversity.assemblyplanner.persistence.model.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;

public record RefreshRequest(@NonNull @NotBlank String refreshToken) {
}
