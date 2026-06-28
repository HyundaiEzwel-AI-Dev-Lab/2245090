package com.ezwel.pms.test;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class TestDtos {
    private TestDtos() {}

    public record TestCaseCreateRequest(
            Long wbsTaskId,
            @NotNull TestStage stage,
            @NotBlank String caseName,
            @NotBlank String procedure,
            @NotBlank String expectedResult
    ) {}

    public record TestExecuteRequest(
            @NotNull TestResult result,
            String actualResult,
            @NotBlank String tester
    ) {}

    public record TestCaseResponse(
            Long id,
            Long projectId,
            Long wbsTaskId,
            TestStage stage,
            String stageLabel,
            String caseName,
            String procedure,
            String expectedResult,
            TestResult result,
            String resultLabel,
            String tester,
            String actualResult
    ) {
        public static TestCaseResponse from(TestCase testCase) {
            return new TestCaseResponse(
                    testCase.getId(),
                    testCase.getProject().getId(),
                    testCase.getWbsTask() == null ? null : testCase.getWbsTask().getId(),
                    testCase.getStage(),
                    testCase.getStage().getLabel(),
                    testCase.getCaseName(),
                    testCase.getProcedure(),
                    testCase.getExpectedResult(),
                    testCase.getResult(),
                    testCase.getResult().getLabel(),
                    testCase.getTester(),
                    testCase.getActualResult()
            );
        }
    }
}
