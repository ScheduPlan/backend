package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.AssemblyTeam;
import de.hofuniversity.assemblyplanner.persistence.model.dto.DescribableResourceRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.EmployeeListItem;
import de.hofuniversity.assemblyplanner.persistence.model.dto.OrderListItem;
import de.hofuniversity.assemblyplanner.persistence.model.dto.TeamDeleteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    Iterable<AssemblyTeam> getTeams();

    AssemblyTeam getTeam(UUID teamId);

    AssemblyTeam createTeam(DescribableResourceRequest teamCreateRequest);

    AssemblyTeam patchTeam(UUID teamId, DescribableResourceRequest patchTeamRequest);

    AssemblyTeam putTeam(UUID teamId, DescribableResourceRequest putTeamRequest);

    TeamDeleteResponse deleteTeam(UUID teamId);

    List<OrderListItem> getOrders(UUID teamId);

    List<EmployeeListItem> getMembers(UUID teamId);
}
