package com.ezwel.pms.project;

import com.ezwel.pms.common.ApiResponse;
import com.ezwel.pms.project.ProjectDtos.ProjectCreateRequest;
import com.ezwel.pms.project.ProjectDtos.ProjectResponse;
import com.ezwel.pms.project.ProjectDtos.ProjectSearchCondition;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ApiResponse<ProjectResponse> create(@Valid @RequestBody ProjectCreateRequest request) {
        return ApiResponse.ok(projectService.create(request));
    }

    @GetMapping
    public ApiResponse<List<ProjectResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) String openMonth
    ) {
        return ApiResponse.ok(projectService.search(new ProjectSearchCondition(keyword, status, openMonth)));
    }

    @GetMapping("/{projectId}")
    public ApiResponse<ProjectResponse> get(@PathVariable Long projectId) {
        return ApiResponse.ok(projectService.get(projectId));
    }
}
