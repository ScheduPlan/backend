package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;
import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.Order;
import de.hofuniversity.assemblyplanner.persistence.model.dto.DescribableResourceRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeListItem;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderListItem;
import de.hofuniversity.assemblyplanner.persistence.model.dto.TeamDeleteResponse;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.TeamDescription;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class TeamServiceImpl implements de.hofuniversity.assemblyplanner.service.api.TeamService {

    private final TeamRepository teamRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamServiceImpl.class);

    public TeamServiceImpl(@Autowired TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public Iterable<AssemblyTeam> getTeams() {
        return teamRepository.findAll();
    }

    @Override
    public AssemblyTeam getTeam(UUID teamId) {
        LOGGER.info("searching team {}", teamId);
        return teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public AssemblyTeam createTeam(DescribableResourceRequest teamCreateRequest) {
        AssemblyTeam team = new AssemblyTeam(
                new TeamDescription(teamCreateRequest.name(), teamCreateRequest.description()), null, null);

        LOGGER.info("creating team using {}", teamCreateRequest);

        return teamRepository.save(team);
    }

    @Override
    public AssemblyTeam patchTeam(UUID teamId, DescribableResourceRequest patchTeamRequest) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        if(patchTeamRequest.name() != null)
            team.getDescription().setName(patchTeamRequest.name());
        if(patchTeamRequest.description() != null)
            team.getDescription().setDescription(patchTeamRequest.description());

        LOGGER.info("updating team {} using patch {}", teamId, patchTeamRequest);

        return teamRepository.save(team);
    }

    @Override
    public AssemblyTeam putTeam(UUID teamId, DescribableResourceRequest putTeamRequest) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(putTeamRequest, team);

        LOGGER.info("updating team {} using update request {}", teamId, putTeamRequest);
        return teamRepository.save(team);
    }

    @Override
    public TeamDeleteResponse deleteTeam(UUID teamId) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        TeamDeleteResponse response = new TeamDeleteResponse(team);
        LOGGER.info("deleting team {}", teamId);
        teamRepository.delete(team);
        return response;
    }

    @Override
    public List<OrderListItem> getOrders(UUID teamId) {
        LOGGER.info("fetching orders for team {}", teamId);

        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        Iterable<Order> orders = team.getOrders();
        return StreamSupport.stream(orders.spliterator(), false)
                .map(OrderListItem::new).toList();
    }

    @Override
    public List<EmployeeListItem> getMembers(UUID teamId) {
        LOGGER.info("fetching members for team {}", teamId);
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        List<Employee> employees = team.getEmployees();
        return employees.stream().map(EmployeeListItem::new).toList();
    }

}
