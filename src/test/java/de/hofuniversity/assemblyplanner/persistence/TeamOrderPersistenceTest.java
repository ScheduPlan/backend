package de.hofuniversity.assemblyplanner.persistence;

import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.OrderState;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.repository.OrderRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import de.hofuniversity.assemblyplanner.util.JpaUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TeamOrderPersistenceTest {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void shouldReflectAssociationBidirectionally() {
        Order o = orderRepository.save(
                new Order(
                        123,
                        "test",
                        1234,
                        1.0,
                        OrderState.PLANNED,
                        null,
                        null,
                        null));

        AssemblyTeam t = teamRepository.save(
                new AssemblyTeam(new Description("test", "test"), null, null));

        o = orderRepository.findById(o.getId()).orElseThrow();
        t = teamRepository.findById(t.getId()).orElseThrow();

        o.setTeam(t);
        orderRepository.save(o);

        JpaUtil.reset(testEntityManager);

        t = teamRepository.findById(t.getId()).orElseThrow();

        assertThat(t.getOrders()).hasSize(1);
    }

}
