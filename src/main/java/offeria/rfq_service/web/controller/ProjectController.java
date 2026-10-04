package offeria.rfq_service.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import offeria.rfq_service.service.ProjectService;
import offeria.rfq_service.web.dto.project.ProjectRequest;
import offeria.rfq_service.web.dto.project.ProjectResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients/{clientId}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @PathVariable UUID clientId,
            @Valid @RequestBody ProjectRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectService.create(clientId, request));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getById(
            @PathVariable UUID clientId,
            @PathVariable UUID projectId
    ) {
        return ResponseEntity.ok(
                projectService.getById(clientId, projectId)
        );
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAll(
            @PathVariable UUID clientId
    ) {
        return ResponseEntity.ok(
                projectService.getAllByClient(clientId)
        );
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable UUID clientId,
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectRequest request
    ) {
        return ResponseEntity.ok(
                projectService.update(
                        clientId,
                        projectId,
                        request
                )
        );
    }
}
