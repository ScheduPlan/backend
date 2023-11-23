package de.hofuniversity.assemblyplanner.persistence.model;

import de.hofuniversity.assemblyplanner.persistence.model.dto.PersonRequest;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;
    protected String firstName;
    protected String lastName;

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName);
    }

    public static void assign(PersonRequest request, Person person, boolean ignoreEmpty) {
        if(person == null || request == null) {
            return;
        }
        if(request.firstName() != null || !ignoreEmpty)
            person.setFirstName(request.firstName());
        if(request.lastName() != null || !ignoreEmpty)
            person.setLastName(request.lastName());
    }
}
