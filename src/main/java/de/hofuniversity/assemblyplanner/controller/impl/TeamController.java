package de.hofuniversity.assemblyplanner.controller.impl;

import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;
import de.hofuniversity.assemblyplanner.persistence.model.Employee;
import de.hofuniversity.assemblyplanner.persistence.model.dto.DescribableResourceRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeListItem;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderListItem;
import de.hofuniversity.assemblyplanner.persistence.model.dto.TeamDeleteResponse;
import de.hofuniversity.assemblyplanner.service.api.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService){
        this.teamService = teamService;
    }

    @GetMapping
    @Operation(summary = "gets all teams")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<AssemblyTeam> getTeams() {
        return teamService.getTeams();
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "gets a team")
    @ResponseStatus(HttpStatus.OK)
    public AssemblyTeam getTeam(@PathVariable UUID teamId) {
        return teamService.getTeam(teamId);
    }

    @PostMapping
    @Operation(summary = "creates a team")
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({"MANAGER", "ADMINISTRATOR"})
    public AssemblyTeam createTeam(@RequestBody @Valid DescribableResourceRequest teamCreateRequest) {
        return teamService.createTeam(teamCreateRequest);
    }

    @PatchMapping("/{teamId}")
    @Operation(summary = "updates a team. NULL values are ignored", responses = {
            @ApiResponse(responseCode = "404", description = "the team was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"MANAGER", "ADMINISTRATOR"})
    public AssemblyTeam patchTeam(@PathVariable UUID teamId, @RequestBody DescribableResourceRequest patchTeamRequest) {
        return teamService.patchTeam(teamId, patchTeamRequest);
    }

    @PutMapping("/{teamId}")
    @Operation(summary = "updates a team", responses = {
            @ApiResponse(responseCode = "404", description = "the team was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"MANAGER", "ADMINISTRATOR"})
    public AssemblyTeam putTeam(@PathVariable UUID teamId, @RequestBody DescribableResourceRequest putTeamRequest) {
        return teamService.putTeam(teamId, putTeamRequest);
    }

    @DeleteMapping("/{teamId}")
    @Operation(summary = "deletes a team", responses = {
            @ApiResponse(responseCode = "404", description = "the team was not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"MANAGER", "ADMINISTRATOR"})
    public TeamDeleteResponse deleteTeam(@PathVariable UUID teamId) {
        return teamService.deleteTeam(teamId);
    }

    @GetMapping("/{teamId}/orders")
    @Operation(summary = "gets all orders for the specified team", responses = {
            @ApiResponse(responseCode = "404", description = "the specified team does not exist")
    })
    @ResponseStatus(HttpStatus.OK)
    public List<OrderListItem> getOrders(@PathVariable UUID teamId) {
        AssemblyTeam team = teamService.getTeam(teamId);
        List<OrderListItem> orderListItems = new ArrayList<>();

        for(var order : team.getOrders()) {
            orderListItems.add(new OrderListItem(order));
        }

        return orderListItems;
    }

    @GetMapping("/{teamId}/members")
    @Operation(summary = "gets all members for the specified team", responses = {
            @ApiResponse(responseCode = "404", description = "the specified team does not exist")
    })
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeListItem> getMembers(@PathVariable UUID teamId) {
        AssemblyTeam team = teamService.getTeam(teamId);
        List<Employee> employees = team.getEmployees();

        return employees.stream().map(EmployeeListItem::new).toList();
    }
}
