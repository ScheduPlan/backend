package de.hofuniversity.assemblyplanner.security.api;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AuthenticationDetails;
import de.hofuniversity.assemblyplanner.persistence.model.dto.TokenDescription;
import de.hofuniversity.assemblyplanner.security.model.TokenType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Map;

public interface AuthenticationService {
    AuthenticationDetails newAccessToken(String refreshToken);

    AuthenticationDetails login(UserDetails user, Map<String, Object> claims);

    String createToken(Employee employee, Map<String, Object> claims);

    String createToken(Employee employee, Map<String, Object> claims, Duration validFor, TokenType type);

    String createToken(Map<String, Object> claims, Employee employee, Duration validity, TokenType type);

    TokenDescription parseToken(String token);
    UsernamePasswordAuthenticationToken toUsernamePasswordAuthenticationToken(TokenDescription token);
}
