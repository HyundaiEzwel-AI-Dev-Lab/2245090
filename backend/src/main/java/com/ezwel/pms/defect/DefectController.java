package com.ezwel.pms.defect;

import com.ezwel.pms.common.ApiResponse;
import com.ezwel.pms.defect.DefectDtos.DefectResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DefectController {
    private final DefectService defectService;

    @GetMapping("/api/projects/{projectId}/defects")
    public ApiResponse<List<DefectResponse>> list(@PathVariable Long projectId) {
        return ApiResponse.ok(defectService.list(projectId));
    }
}
