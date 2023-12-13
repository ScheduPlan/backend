package de.hofuniversity.assemblyplanner.security.api;

import de.hofuniversity.assemblyplanner.persistence.model.dto.AuthenticationDetails;
import de.hofuniversity.assemblyplanner.persistence.model.dto.TokenDescription;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface AuthenticationService {
    AuthenticationDetails createToken(UserDetails userDetails, Map<String, Object> payload);
    TokenDescription parseToken(String token);
    UsernamePasswordAuthenticationToken toUsernamePasswordAuthenticationToken(TokenDescription token);
}
