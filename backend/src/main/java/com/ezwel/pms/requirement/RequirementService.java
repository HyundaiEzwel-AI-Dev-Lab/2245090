package com.ezwel.pms.requirement;

import com.ezwel.pms.common.BusinessException;
import com.ezwel.pms.history.HistoryService;
import com.ezwel.pms.project.Project;
import com.ezwel.pms.project.ProjectService;
import com.ezwel.pms.requirement.RequirementDtos.RequirementCreateRequest;
import com.ezwel.pms.requirement.RequirementDtos.RequirementResponse;
import com.ezwel.pms.wbs.WbsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequirementService {
    private final ProjectService projectService;
    private final RequirementRepository requirementRepository;
    private final HistoryService historyService;
    private final @Lazy WbsService wbsService;

    @Transactional
    public RequirementResponse create(Long projectId, RequirementCreateRequest request) {
        Project project = projectService.findProject(projectId);
        Requirement requirement = Requirement.builder()
                .project(project)
                .title(request.title())
                .systemType(request.systemType())
                .businessType(request.businessType())
                .menuName(request.menuName())
                .description(request.description())
                .status(RequirementStatus.DRAFT)
                .build();
        Requirement saved = requirementRepository.save(requirement);
        historyService.record(projectId, "REQUIREMENT", saved.getId(), "CREATE", "요구사항이 등록되었습니다.", project.getRequester());
        return RequirementResponse.from(saved);
    }

    @Transactional
    public RequirementResponse confirm(Long requirementId) {
        Requirement requirement = findRequirement(requirementId);
        if (requirement.isConfirmed()) {
            throw new BusinessException("이미 확정된 요구사항입니다.");
        }
        requirement.confirm();
        wbsService.createDefaultTasksFromRequirement(requirement);
        historyService.record(requirement.getProject().getId(), "REQUIREMENT", requirement.getId(), "CONFIRM", "요구사항이 확정되어 WBS가 생성되었습니다.", "system");
        return RequirementResponse.from(requirement);
    }

    public List<RequirementResponse> list(Long projectId) {
        return requirementRepository.findByProjectIdOrderByIdDesc(projectId).stream()
                .map(RequirementResponse::from)
                .toList();
    }

    public Requirement findRequirement(Long requirementId) {
        return requirementRepository.findById(requirementId)
                .orElseThrow(() -> new BusinessException("요구사항을 찾을 수 없습니다."));
    }
}
