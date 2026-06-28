package com.ezwel.pms.test;

import com.ezwel.pms.common.BusinessException;
import com.ezwel.pms.defect.DefectService;
import com.ezwel.pms.history.HistoryService;
import com.ezwel.pms.project.Project;
import com.ezwel.pms.project.ProjectService;
import com.ezwel.pms.test.TestDtos.TestCaseCreateRequest;
import com.ezwel.pms.test.TestDtos.TestCaseResponse;
import com.ezwel.pms.test.TestDtos.TestExecuteRequest;
import com.ezwel.pms.wbs.WbsTask;
import com.ezwel.pms.wbs.WbsTaskRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestCaseService {
    private final TestCaseRepository testCaseRepository;
    private final ProjectService projectService;
    private final WbsTaskRepository wbsTaskRepository;
    private final DefectService defectService;
    private final HistoryService historyService;

    @Transactional
    public TestCaseResponse create(Long projectId, TestCaseCreateRequest request) {
        Project project = projectService.findProject(projectId);
        WbsTask wbsTask = request.wbsTaskId() == null
                ? null
                : wbsTaskRepository.findById(request.wbsTaskId())
                        .orElseThrow(() -> new BusinessException("WBS 업무를 찾을 수 없습니다."));

        TestCase testCase = TestCase.builder()
                .project(project)
                .wbsTask(wbsTask)
                .stage(request.stage())
                .caseName(request.caseName())
                .procedure(request.procedure())
                .expectedResult(request.expectedResult())
                .result(TestResult.NOT_RUN)
                .build();

        TestCase saved = testCaseRepository.save(testCase);
        historyService.record(projectId, "TEST_CASE", saved.getId(), "CREATE", "테스트 케이스가 등록되었습니다.", "system");
        return TestCaseResponse.from(saved);
    }

    @Transactional
    public void createDefaultUnitTestCase(WbsTask task) {
        TestCase testCase = TestCase.builder()
                .project(task.getProject())
                .wbsTask(task)
                .stage(TestStage.UNIT)
                .caseName(task.getTaskName() + " 검증")
                .procedure("기능을 실행하고 정상 처리 여부를 확인한다.")
                .expectedResult("오류 없이 정상 처리된다.")
                .result(TestResult.NOT_RUN)
                .build();

        testCaseRepository.save(testCase);
    }

    @Transactional
    public TestCaseResponse execute(Long testCaseId, TestExecuteRequest request) {
        TestCase testCase = findTestCase(testCaseId);
        testCase.execute(request.result(), request.actualResult(), request.tester());
        historyService.record(testCase.getProject().getId(), "TEST_CASE", testCase.getId(), "EXECUTE", "테스트가 수행되었습니다.", request.tester());

        if (testCase.isFailed()) {
            defectService.createFromFailedTest(testCase, request.actualResult(), request.tester());
        }

        return TestCaseResponse.from(testCase);
    }

    public List<TestCaseResponse> list(Long projectId, TestStage stage) {
        List<TestCase> testCases = stage == null
                ? testCaseRepository.findByProjectIdOrderByIdDesc(projectId)
                : testCaseRepository.findByProjectIdAndStageOrderByIdDesc(projectId, stage);

        return testCases.stream().map(TestCaseResponse::from).toList();
    }

    private TestCase findTestCase(Long testCaseId) {
        return testCaseRepository.findById(testCaseId)
                .orElseThrow(() -> new BusinessException("테스트 케이스를 찾을 수 없습니다."));
    }
}