package de.hofuniversity.assemblyplanner.security.impl;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.security.model.AuthenticationDetails;
import de.hofuniversity.assemblyplanner.security.model.TokenDescription;
import de.hofuniversity.assemblyplanner.security.api.AuthenticationService;
import de.hofuniversity.assemblyplanner.security.model.TokenType;
import de.hofuniversity.assemblyplanner.service.EmployeeUserDetailsAdapter;
import de.hofuniversity.assemblyplanner.service.UserService;
import de.hofuniversity.assemblyplanner.util.DateUtil;
import de.hofuniversity.assemblyplanner.util.KeyUtil;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtAuthenticationService implements AuthenticationService {

    private final Duration validity;
    private final Duration refreshValidity;
    private final Key signKey;
    private final JwtParser parser;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationService(@Value("${auth.validity.minutes:60}") int validityInMinutes,
                                    @Value("${auth.validity.sign-key:#{null}}") String signKey,
                                    @Value("${auth.refresh.validity.hours:1}") int refreshValidityInHours,
                                    @Autowired UserService userService,
                                    @Autowired AuthenticationManager authenticationManager) {

        this.validity = Duration.ofMinutes(validityInMinutes);
        this.refreshValidity = Duration.ofHours(refreshValidityInHours);
        this.authenticationManager = authenticationManager;

        if(signKey == null)
            this.signKey = KeyUtil.randomKey(Jwts.SIG.HS512);
        else
            this.signKey = KeyUtil.toKey(signKey);

        this.parser = Jwts.parser().verifyWith((SecretKey) this.signKey).build();
        this.userService = userService;
    }

    private void throwIfTokenIsSuperseded(Employee employee, TokenDescription token) {
        if(employee == null || employee.getUser() == null)
            return;

        Date pwChangedDate = employee.getUser().getLastPasswordChange();
        if(pwChangedDate == null || token.issuedAt().after(pwChangedDate))
            return;

        throw new AccessDeniedException("this token is invalid. The password has been changed after the issuedAt date.");
    }

    @Override
    public AuthenticationDetails newAccessToken(String refreshToken) {
        TokenDescription description = parseToken(refreshToken);
        if(!TokenType.REFRESH.toString().equals(description.claims().get("type"))) {
            throw new AccessDeniedException("not a refresh token");
        }

        Employee employee = userService.loadUserByToken(description);

        if(!employee.getUser().isEnabled()
                || !employee.getUser().isAccountNonLocked()
                || !employee.getUser().isAccountNonExpired()
                || !employee.getUser().isCredentialsNonExpired()) {
            throw new AccessDeniedException("user is disabled or required manual refresh");
        }

        Date pwChangedDate = employee.getUser().getLastPasswordChange();
        if(pwChangedDate != null && description.issuedAt().before(pwChangedDate))
            throw new AccessDeniedException("this token is invalid. The password has been changed after the issuedAt date.");

        String accessToken = createToken(employee, Map.of());

        return new AuthenticationDetails(employee, accessToken, refreshToken);
    }

    @Override
    public AuthenticationDetails login(UserDetails user, Map<String, Object> claims) {
        Employee employee;
        if(user instanceof EmployeeUserDetailsAdapter ea) {
            employee = ea.getEmployee();
        }
        else {
            employee = userService.loadEmployeeByUsername(user.getUsername());
        }

        String accessToken = createToken(employee, claims);
        String refreshToken = createToken(employee, Map.of(), refreshValidity, TokenType.REFRESH);

        return new AuthenticationDetails(employee, accessToken, refreshToken);
    }

    @Override
    public String createToken(Employee employee, Map<String, Object> claims) {
        return createToken(employee, claims, validity, TokenType.ACCESS);
    }

    @Override
    public String createToken(Employee employee, Map<String, Object> claims, Duration validFor, TokenType type) {
        return createToken(claims, employee, validFor, type);
    }

    @Override
    public String createToken(Map<String, Object> claims, Employee employee, Duration validity, TokenType type) {
        Map<String, Object> actualClaims = new HashMap<>(claims);

        if(!actualClaims.containsKey("userId"))
            actualClaims.put("userId", employee.getId());
        if(!actualClaims.containsKey("type"))
            actualClaims.put("type", type.toString());

        return Jwts.builder()
                .claims(actualClaims)
                .subject(employee.getUser().getUsername())
                .issuedAt(new Date())
                .expiration(DateUtil.toDate(Instant.now().plus(validity)))
                .signWith(signKey)
                .compact();
    }

    @Override
    public TokenDescription parseToken(String token) {
        var data = parser.parseSignedClaims(token).getPayload();
        return new TokenDescription(data);
    }

    @Override
    public UsernamePasswordAuthenticationToken toUsernamePasswordAuthenticationToken(TokenDescription token) {
        EmployeeUserDetailsAdapter user = new EmployeeUserDetailsAdapter(userService.loadUserByToken(token));
        throwIfTokenIsSuperseded(user.getEmployee(), token);
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    public AuthenticationDetails login(String username, String password) {
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return login((UserDetails) auth.getPrincipal(), Map.of());
    }
}
