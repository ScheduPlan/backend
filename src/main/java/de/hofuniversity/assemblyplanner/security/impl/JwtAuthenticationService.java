package de.hofuniversity.assemblyplanner.security.impl;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.dto.AuthenticationDetails;
import de.hofuniversity.assemblyplanner.persistence.model.dto.TokenDescription;
import de.hofuniversity.assemblyplanner.security.api.AuthenticationService;
import de.hofuniversity.assemblyplanner.service.EmployeeUserDetailsAdapter;
import de.hofuniversity.assemblyplanner.service.UserService;
import de.hofuniversity.assemblyplanner.util.DateUtil;
import de.hofuniversity.assemblyplanner.util.KeyUtil;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final Key signKey;
    private final JwtParser parser;
    private final UserService userService;

    public JwtAuthenticationService(@Value("${auth.validity.minutes:60}") int validityInMinutes,
                                    @Value("${auth.validity.sign-key:#{null}}") String signKey,
                                    @Autowired UserService userService) {
        this.validity = Duration.ofMinutes(validityInMinutes);
        if(signKey == null)
            this.signKey = KeyUtil.randomKey(Jwts.SIG.HS512);
        else
            this.signKey = KeyUtil.toKey(signKey);
        this.parser = Jwts.parser().verifyWith((SecretKey) this.signKey).build();
        this.userService = userService;
    }

    public AuthenticationDetails createToken(UserDetails user, Map<String, Object> claims) {
        Employee employee;
        if(user instanceof EmployeeUserDetailsAdapter ea) {
            employee = ea.getEmployee();
        }
        else {
            employee = userService.loadEmployeeByUsername(user.getUsername());
        }

        Map<String, Object> actualClaims = new HashMap<>(claims);

        if(!actualClaims.containsKey("userId"))
            actualClaims.put("userId", employee.getId());

        String token = Jwts.builder()
                .claims(actualClaims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(DateUtil.toDate(Instant.now().plus(validity)))
                .signWith(signKey)
                .compact();

        return new AuthenticationDetails(employee, token);
    }

    public TokenDescription parseToken(String token) {
        var data = parser.parseSignedClaims(token).getPayload();
        return new TokenDescription(data);
    }

    @Override
    public UsernamePasswordAuthenticationToken toUsernamePasswordAuthenticationToken(TokenDescription token) {
        UserDetails user = userService.loadUserByToken(token);
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
}
