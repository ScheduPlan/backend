package de.hofuniversity.assemblyplanner.persistence.model.dto;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public record TokenDescription(Date issuedAt, String subject, Date expiry, Map<String, Object> claims, UUID userId) {
    public TokenDescription(Claims claims) {
        this(
                claims.getIssuedAt(),
                claims.getSubject(),
                claims.getExpiration(),
                Map.copyOf(claims),
                (UUID) claims.get("userId"));
    }
}
