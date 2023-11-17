package de.hofuniversity.assemblyplanner.controller;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;
import de.hofuniversity.assemblyplanner.persistence.model.dto.DescribableResourceRequest;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.repository.TeamRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public AssemblyTeam createTeam(@RequestBody DescribableResourceRequest teamCreateRequest) {
        AssemblyTeam team = new AssemblyTeam(
                new Description(teamCreateRequest.name(), teamCreateRequest.description()), null);

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
    public AssemblyTeam deleteTeam(@PathVariable UUID teamId) {
        AssemblyTeam team = teamRepository.findById(teamId).orElseThrow(ResourceNotFoundException::new);
        teamRepository.delete(team);
        return team;
    }
}
