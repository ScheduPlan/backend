package de.hofuniversity.assemblyplanner.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Embeddable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;

@Embeddable
public class User implements Serializable, UserDetails {
    public static final String ROLE_PREFIX = "ROLE_";
    private String userName;
    private String password;
    private String email;
    private Role role;
    private Date expiryDate;
    private boolean locked;
    private boolean enabled = true;

    public User(String userName, String email, String password, Role role, Date expiryDate) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.expiryDate = expiryDate;
    }

    public User(String userName, String email, String password, Role role) {
        this(userName, email, password, role, null);
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
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + Role.ADMINISTRATOR.toString()));
            case MANAGER:
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + Role.MANAGER.toString()));
            case FITTER:
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + Role.FITTER.toString()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
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
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return locked == user.locked && Objects.equals(userName, user.userName) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && role == user.role && Objects.equals(expiryDate, user.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password, email, role, expiryDate, locked);
    }
}
