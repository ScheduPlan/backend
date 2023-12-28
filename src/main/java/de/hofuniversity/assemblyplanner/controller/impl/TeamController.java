package de.hofuniversity.assemblyplanner.controller.impl;

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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamRepository teamRepository;

    public TeamController(@Autowired TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @GetMapping
    @Operation(summary = "gets all teams")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<AssemblyTeam> getTeams() {
        return teamRepository.findAll();
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "gets a team")
    @ResponseStatus(HttpStatus.OK)
    public AssemblyTeam getTeam(@PathVariable UUID teamId) {
        return teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping
    @Operation(summary = "creates a team")
    @ResponseStatus(HttpStatus.CREATED)
    public AssemblyTeam createTeam(@RequestBody @Valid DescribableResourceRequest teamCreateRequest) {
        AssemblyTeam team = new AssemblyTeam(
                new Description(teamCreateRequest.name(), teamCreateRequest.description()), null, null);

        return teamRepository.save(team);
    }

    @PatchMapping("/{teamId}")
    @Operation(summary = "updates a team. NULL values are ignored", responses = {
            @ApiResponse(responseCode = "404", description = "the team was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public AssemblyTeam patchTeam(@PathVariable UUID teamId, @RequestBody DescribableResourceRequest patchTeamRequest) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        if(patchTeamRequest.name() != null)
            team.getDescription().setName(patchTeamRequest.name());
        if(patchTeamRequest.description() != null)
            team.getDescription().setDescription(patchTeamRequest.description());

        return teamRepository.save(team);
    }

    @PutMapping("/{teamId}")
    @Operation(summary = "updates a team", responses = {
            @ApiResponse(responseCode = "404", description = "the team was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public AssemblyTeam putTeam(@PathVariable UUID teamId, @RequestBody DescribableResourceRequest putTeamRequest) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(putTeamRequest, team);
        return teamRepository.save(team);
    }

    @DeleteMapping("/{teamId}")
    @Operation(summary = "deletes a team", responses = {
            @ApiResponse(responseCode = "404", description = "the team was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    public TeamDeleteResponse deleteTeam(@PathVariable UUID teamId) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        TeamDeleteResponse response = new TeamDeleteResponse(team);
        teamRepository.delete(team);
        return response;
    }

    @GetMapping("/{teamId}/orders")
    @Operation(summary = "gets all orders for the specified team", responses = {
            @ApiResponse(responseCode = "404", description = "the specified team does not exist")
    })
    @ResponseStatus(HttpStatus.OK)
    public List<OrderListItem> getOrders(@PathVariable UUID teamId) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        Iterable<Order> orders = team.getOrders();
        return StreamSupport.stream(orders.spliterator(), false)
                .map(OrderListItem::new).toList();
    }

    @GetMapping("/{teamId}/members")
    @Operation(summary = "gets all members for the specified team", responses = {
            @ApiResponse(responseCode = "404", description = "the specified team does not exist")
    })
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeListItem> getMembers(@PathVariable UUID teamId) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        List<Employee> employees = team.getEmployees();
        return employees.stream().map(EmployeeListItem::new).toList();
    }

}
