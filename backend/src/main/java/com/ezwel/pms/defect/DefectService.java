package com.ezwel.pms.defect;

import com.ezwel.pms.defect.DefectDtos.DefectResponse;
import com.ezwel.pms.history.HistoryService;
import com.ezwel.pms.test.TestCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefectService {
    private final DefectRepository defectRepository;
    private final HistoryService historyService;

    @Transactional
    public DefectResponse createFromFailedTest(TestCase testCase, String actualResult, String tester) {
        Defect defect = Defect.builder()
                .project(testCase.getProject())
                .testCase(testCase)
                .title("[테스트 오류] " + testCase.getCaseName())
                .description(actualResult == null || actualResult.isBlank() ? "테스트 실패로 자동 등록된 결함입니다." : actualResult)
                .assignee(resolveDefectAssignee(testCase))
                .status(DefectStatus.OPEN)
                .build();
        Defect saved = defectRepository.save(defect);
        historyService.record(testCase.getProject().getId(), "DEFECT", saved.getId(), "CREATE", "테스트 실패로 결함이 등록되었습니다.", tester);
        return DefectResponse.from(saved);
    }

    public List<DefectResponse> list(Long projectId) {
        return defectRepository.findByProjectIdOrderByIdDesc(projectId).stream()
                .map(DefectResponse::from)
                .toList();
    }

    private String resolveDefectAssignee(TestCase testCase) {
        return testCase.getWbsTask() == null ? "김현대" : testCase.getWbsTask().getAssignee();
    }
}
