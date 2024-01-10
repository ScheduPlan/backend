package de.hofuniversity.assemblyplanner.security.filter;

import de.hofuniversity.assemblyplanner.security.api.AuthenticationService;
import de.hofuniversity.assemblyplanner.security.model.TokenDescription;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilterComponent extends OncePerRequestFilter {

    private static final String BEARER_IDENTIFIER = "Bearer ";
    private static final String OPTIONS_METHOD = "OPTIONS";
    private final AuthenticationService authenticationService;

    public JwtAuthFilterComponent(@Autowired AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if(token == null || !token.startsWith(BEARER_IDENTIFIER)) {
            filterChain.doFilter(request, response);
            return;
        }

        if(request.getMethod().equals(OPTIONS_METHOD)) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(BEARER_IDENTIFIER.length());
        try {
            TokenDescription tokenDescription = authenticationService.parseToken(token);
            request.setAttribute("token", tokenDescription);
            SecurityContextHolder.getContext()
                    .setAuthentication(authenticationService.toUsernamePasswordAuthenticationToken(tokenDescription));
        } catch (AccessDeniedException | InsufficientAuthenticationException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }

        filterChain.doFilter(request, response);
    }
}
