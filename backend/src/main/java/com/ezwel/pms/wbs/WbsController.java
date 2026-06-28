package com.ezwel.pms.wbs;

import com.ezwel.pms.common.ApiResponse;
import com.ezwel.pms.wbs.WbsDtos.ScheduleUpdateRequest;
import com.ezwel.pms.wbs.WbsDtos.WbsTaskResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WbsController {
    private final WbsService wbsService;

    @GetMapping("/api/projects/{projectId}/wbs-tasks")
    public ApiResponse<List<WbsTaskResponse>> listByProject(@PathVariable Long projectId) {
        return ApiResponse.ok(wbsService.listByProject(projectId));
    }

    @GetMapping("/api/my-work/wbs-tasks")
    public ApiResponse<List<WbsTaskResponse>> listMyTasks(@RequestParam(defaultValue = "김현대") String assignee) {
        return ApiResponse.ok(wbsService.listMyTasks(assignee));
    }

    @PatchMapping("/api/wbs-tasks/{taskId}/schedule")
    public ApiResponse<WbsTaskResponse> updateSchedule(@PathVariable Long taskId, @Valid @RequestBody ScheduleUpdateRequest request) {
        return ApiResponse.ok(wbsService.updateSchedule(taskId, request));
    }

    @PatchMapping("/api/wbs-tasks/{taskId}/start")
    public ApiResponse<WbsTaskResponse> start(@PathVariable Long taskId) {
        return ApiResponse.ok(wbsService.start(taskId));
    }

    @PatchMapping("/api/wbs-tasks/{taskId}/complete")
    public ApiResponse<WbsTaskResponse> complete(@PathVariable Long taskId) {
        return ApiResponse.ok(wbsService.complete(taskId));
    }
}
