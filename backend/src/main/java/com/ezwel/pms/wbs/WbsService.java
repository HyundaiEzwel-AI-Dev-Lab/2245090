package com.ezwel.pms.wbs;

import com.ezwel.pms.common.BusinessException;
import com.ezwel.pms.history.HistoryService;
import com.ezwel.pms.project.Project;
import com.ezwel.pms.requirement.Requirement;
import com.ezwel.pms.test.TestCaseService;
import com.ezwel.pms.wbs.WbsDtos.ScheduleUpdateRequest;
import com.ezwel.pms.wbs.WbsDtos.WbsTaskResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WbsService {
    private final WbsTaskRepository wbsTaskRepository;
    private final HistoryService historyService;
    private final @Lazy TestCaseService testCaseService;

    @Transactional
    public void createDefaultTasksFromRequirement(Requirement requirement) {
        if (wbsTaskRepository.existsByRequirementId(requirement.getId())) {
            return;
        }

        Project project = requirement.getProject();
        List<WorkType> defaultWorkTypes = List.of(WorkType.PLANNING, WorkType.DEVELOPMENT, WorkType.UNIT_TEST);
        for (WorkType workType : defaultWorkTypes) {
            WbsTask task = WbsTask.builder()
                    .project(project)
                    .requirement(requirement)
                    .taskName(requirement.getTitle() + " " + workType.getLabel())
                    .workType(workType)
                    .assignee(resolveDefaultAssignee(project, workType))
                    .progressRate(0)
                    .status(WbsStatus.READY)
                    .priority("보통")
                    .difficulty("중")
                    .unitTestEnabled(true)
                    .build();
            WbsTask saved = wbsTaskRepository.save(task);
            if (WorkType.UNIT_TEST.equals(workType)) {
                testCaseService.createDefaultUnitTestCase(saved);
            }
        }
    }

    @Transactional
    public WbsTaskResponse updateSchedule(Long taskId, ScheduleUpdateRequest request) {
        validateSchedule(request.planStartDate(), request.planEndDate());
        WbsTask task = findTask(taskId);
        task.updateSchedule(request.planStartDate(), request.planEndDate(), request.note());
        historyService.record(task.getProject().getId(), "WBS", task.getId(), "UPDATE_SCHEDULE", "WBS 일정이 변경되었습니다.", task.getAssignee());
        return WbsTaskResponse.from(task, LocalDate.now());
    }

    @Transactional
    public WbsTaskResponse start(Long taskId) {
        WbsTask task = findTask(taskId);
        task.start(LocalDate.now());
        historyService.record(task.getProject().getId(), "WBS", task.getId(), "START", "WBS 작업이 착수되었습니다.", task.getAssignee());
        return WbsTaskResponse.from(task, LocalDate.now());
    }

    @Transactional
    public WbsTaskResponse complete(Long taskId) {
        WbsTask task = findTask(taskId);
        task.complete(LocalDate.now());
        historyService.record(task.getProject().getId(), "WBS", task.getId(), "COMPLETE", "WBS 작업이 완료되었습니다.", task.getAssignee());
        return WbsTaskResponse.from(task, LocalDate.now());
    }

    public List<WbsTaskResponse> listByProject(Long projectId) {
        LocalDate today = LocalDate.now();
        return wbsTaskRepository.findByProjectIdOrderByIdDesc(projectId).stream()
                .map(task -> WbsTaskResponse.from(task, today))
                .toList();
    }

    public List<WbsTaskResponse> listMyTasks(String assignee) {
        LocalDate today = LocalDate.now();
        return wbsTaskRepository.findByAssigneeOrderByPlanEndDateAsc(assignee).stream()
                .map(task -> WbsTaskResponse.from(task, today))
                .toList();
    }

    public WbsTask findTask(Long taskId) {
        return wbsTaskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("WBS 업무를 찾을 수 없습니다."));
    }

    private String resolveDefaultAssignee(Project project, WorkType workType) {
        if (WorkType.PLANNING.equals(workType)) {
            return project.getRequester();
        }
        if (WorkType.UNIT_TEST.equals(workType)) {
            return "테스터";
        }
        return "김현대";
    }

    private void validateSchedule(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("계획 시작일은 종료일보다 늦을 수 없습니다.");
        }
    }
}
