package de.hofuniversity.assemblyplanner.persistence.model.dto;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.Map;

public record TokenDescription(Date issuedAt, String subject, Date expiry, Map<String, Object> claims) {
    public TokenDescription(Claims claims) {
        this(claims.getIssuedAt(), claims.getSubject(), claims.getExpiration(), Map.copyOf(claims));
    }
}
