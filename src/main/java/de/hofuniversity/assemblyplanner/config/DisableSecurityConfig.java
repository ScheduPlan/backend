package de.hofuniversity.assemblyplanner.config;

import de.hofuniversity.assemblyplanner.security.api.AuthenticationService;
import de.hofuniversity.assemblyplanner.security.filter.JwtAuthFilterComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class DisableSecurityConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authenticationProvider, AuthenticationService authenticationService) throws Exception {
        http.authorizeHttpRequests(x -> x.requestMatchers(AntPathRequestMatcher.antMatcher("/auth/login")).permitAll())
                .authorizeHttpRequests(x -> x.requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**")).permitAll())
                .authorizeHttpRequests(x -> x.requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**")).permitAll())
                .authorizeHttpRequests(x -> x.requestMatchers(AntPathRequestMatcher.antMatcher("/error")).permitAll())
                .authorizeHttpRequests(x -> x.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.OPTIONS)).permitAll())
                .authorizeHttpRequests(x -> x.requestMatchers(AntPathRequestMatcher.antMatcher("/**")).authenticated())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(new JwtAuthFilterComponent(authenticationService), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
