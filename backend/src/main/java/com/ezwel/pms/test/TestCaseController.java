package com.ezwel.pms.test;

import com.ezwel.pms.common.ApiResponse;
import com.ezwel.pms.test.TestDtos.TestCaseCreateRequest;
import com.ezwel.pms.test.TestDtos.TestCaseResponse;
import com.ezwel.pms.test.TestDtos.TestExecuteRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TestCaseController {
    private final TestCaseService testCaseService;

    @GetMapping("/api/projects/{projectId}/test-cases")
    public ApiResponse<List<TestCaseResponse>> list(@PathVariable Long projectId, @RequestParam(required = false) TestStage stage) {
        return ApiResponse.ok(testCaseService.list(projectId, stage));
    }

    @PostMapping("/api/projects/{projectId}/test-cases")
    public ApiResponse<TestCaseResponse> create(@PathVariable Long projectId, @Valid @RequestBody TestCaseCreateRequest request) {
        return ApiResponse.ok(testCaseService.create(projectId, request));
    }

    @PatchMapping("/api/test-cases/{testCaseId}/execute")
    public ApiResponse<TestCaseResponse> execute(@PathVariable Long testCaseId, @Valid @RequestBody TestExecuteRequest request) {
        return ApiResponse.ok(testCaseService.execute(testCaseId, request));
    }
}
