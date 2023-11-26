package de.hofuniversity.assemblyplanner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//@Configuration
//@EnableMethodSecurity
//@EnableWebSecurity
public class SecurityConfig {

    @Value("${argon2.salt-length:32}")
    private int saltLength;

    @Value("${argon2.hash-length:64}")
    private int hashLength;

    @Value("${argon2.parallelism:1}")
    private int parallelism;

    @Value("${argon2.memory-cost:19456")
    private int memoryCost;

    @Value("${argon2.iterations:2")
    private int iterations;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memoryCost, iterations);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new DaoAuthenticationProvider();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
