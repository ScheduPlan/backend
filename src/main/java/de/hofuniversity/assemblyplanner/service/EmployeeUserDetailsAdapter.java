package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

public class EmployeeUserDetailsAdapter implements UserDetails {
    private final Employee employee;

    public EmployeeUserDetailsAdapter(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return employee.getUser().getAuthorities();
    }

    @Override
    public String getPassword() {
        return employee.getUser().getPassword();
    }

    @Override
    public String getUsername() {
        return employee.getUser().getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return employee.getUser().isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return employee.getUser().isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return employee.getUser().isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return employee.getUser().isEnabled();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeUserDetailsAdapter that = (EmployeeUserDetailsAdapter) o;
        return Objects.equals(employee, that.employee);
    }

    @Override
    public String toString() {
        return "EmployeeUserDetailsAdapter{" +
                "employee=" + employee +
                '}';
    }
}
