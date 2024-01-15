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
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class TeamServiceImpl implements de.hofuniversity.assemblyplanner.service.api.TeamService {

    private final TeamRepository teamRepository;

    public TeamServiceImpl(@Autowired TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public Iterable<AssemblyTeam> getTeams() {
        return teamRepository.findAll();
    }

    @Override
    public AssemblyTeam getTeam(UUID teamId) {
        return teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public AssemblyTeam createTeam(DescribableResourceRequest teamCreateRequest) {
        AssemblyTeam team = new AssemblyTeam(
                new Description(teamCreateRequest.name(), teamCreateRequest.description()), null, null);

        return teamRepository.save(team);
    }

    @Override
    public AssemblyTeam patchTeam(UUID teamId, DescribableResourceRequest patchTeamRequest) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        if(patchTeamRequest.name() != null)
            team.getDescription().setName(patchTeamRequest.name());
        if(patchTeamRequest.description() != null)
            team.getDescription().setDescription(patchTeamRequest.description());

        return teamRepository.save(team);
    }

    @Override
    public AssemblyTeam putTeam(UUID teamId, DescribableResourceRequest putTeamRequest) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(putTeamRequest, team);
        return teamRepository.save(team);
    }

    @Override
    public TeamDeleteResponse deleteTeam(UUID teamId) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        TeamDeleteResponse response = new TeamDeleteResponse(team);
        teamRepository.delete(team);
        return response;
    }

    @Override
    public List<OrderListItem> getOrders(UUID teamId) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        Iterable<Order> orders = team.getOrders();
        return StreamSupport.stream(orders.spliterator(), false)
                .map(OrderListItem::new).toList();
    }

    @Override
    public List<EmployeeListItem> getMembers(UUID teamId) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        List<Employee> employees = team.getEmployees();
        return employees.stream().map(EmployeeListItem::new).toList();
    }

}
