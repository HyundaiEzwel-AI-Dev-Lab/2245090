package com.ezwel.pms.history;

import com.ezwel.pms.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChangeHistoryController {
    private final ChangeHistoryRepository changeHistoryRepository;

    @GetMapping("/api/projects/{projectId}/change-histories")
    public ApiResponse<List<ChangeHistory>> list(@PathVariable Long projectId) {
        return ApiResponse.ok(changeHistoryRepository.findByProjectIdOrderByIdDesc(projectId));
    }
}
