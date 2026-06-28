package com.ezwel.pms.requirement;

import com.ezwel.pms.common.ApiResponse;
import com.ezwel.pms.requirement.RequirementDtos.RequirementCreateRequest;
import com.ezwel.pms.requirement.RequirementDtos.RequirementResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RequirementController {
    private final RequirementService requirementService;

    @GetMapping("/api/projects/{projectId}/requirements")
    public ApiResponse<List<RequirementResponse>> list(@PathVariable Long projectId) {
        return ApiResponse.ok(requirementService.list(projectId));
    }

    @PostMapping("/api/projects/{projectId}/requirements")
    public ApiResponse<RequirementResponse> create(@PathVariable Long projectId, @Valid @RequestBody RequirementCreateRequest request) {
        return ApiResponse.ok(requirementService.create(projectId, request));
    }

    @PatchMapping("/api/requirements/{requirementId}/confirm")
    public ApiResponse<RequirementResponse> confirm(@PathVariable Long requirementId) {
        return ApiResponse.ok(requirementService.confirm(requirementId));
    }
}
