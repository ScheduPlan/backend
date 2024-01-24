package de.hofuniversity.assemblyplanner.persistence;

import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.TeamDescription;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DataJpaTest
public class TeamPersistenceTest {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void shouldFindCreatedTeam() {
        testEntityManager.persist(new AssemblyTeam(new TeamDescription("test", "test"), List.of(), List.of()));
        testEntityManager.flush();

        assertThat(teamRepository.findAll())
                .hasSize(1)
                .first().hasNoNullFieldsOrProperties();
    }

    @Test
    public void shouldCreateTeam() {
        assertThatCode(() -> teamRepository.save(new AssemblyTeam(new TeamDescription("test", "test"), null, null)))
                .doesNotThrowAnyException();

        assertThat(teamRepository.findAll()).hasSize(1);
    }
}
