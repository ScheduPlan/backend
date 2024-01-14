package de.hofuniversity.assemblyplanner.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import static de.hofuniversity.assemblyplanner.service.UserService.sanitizeRoleString;

@Embeddable
public class User implements Serializable, UserDetails {
    public static final String ROLE_PREFIX = "ROLE_";

    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    private Role role;
    private Date expiryDate;
    private boolean locked;
    private boolean enabled = true;
    private Date lastPasswordChange = new Date();

    public User(String username, String email, String password, Role role, Date expiryDate) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.expiryDate = expiryDate;
    }

    public User(String username, String email, String password, Role role) {
        this(username, email, password, role, null);
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>(3);
        switch (role) {
            case ADMINISTRATOR:
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + Role.ADMINISTRATOR));
            case MANAGER:
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + Role.MANAGER));
            case FITTER:
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + Role.FITTER));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override @JsonIgnore
    public boolean isAccountNonExpired() {
        return expiryDate == null || expiryDate.after(new Date());
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return expiryDate == null || expiryDate.after(new Date());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setPassword(String password) {
        this.password = password;
        this.lastPasswordChange = new Date();
        this.expiryDate = null;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Date getLastPasswordChange() {
        return lastPasswordChange;
    }

    public boolean isSuperiorTo(User other) {
        return this.getRole().ordinal() > other.getRole().ordinal();
    }

    public boolean isInferiorTo(User other) {
        return this.getRole().ordinal() < other.getRole().ordinal();
    }

    public boolean hasEqualRoleTo(User other) {
        return this.getRole() == other.getRole();
    }

    public boolean hasRole(Role role) {
        String roleStr = role.toString();
        return this.getAuthorities().stream()
                .anyMatch(r -> sanitizeRoleString(r.getAuthority()).equals(roleStr));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return locked == user.locked && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && role == user.role && Objects.equals(expiryDate, user.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email, role, expiryDate, locked);
    }
}
