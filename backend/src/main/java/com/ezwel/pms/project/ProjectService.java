package com.ezwel.pms.project;

import com.ezwel.pms.common.BusinessException;
import com.ezwel.pms.history.HistoryService;
import com.ezwel.pms.project.ProjectDtos.ProjectCreateRequest;
import com.ezwel.pms.project.ProjectDtos.ProjectResponse;
import com.ezwel.pms.project.ProjectDtos.ProjectSearchCondition;
import jakarta.persistence.criteria.Predicate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final HistoryService historyService;

    @Transactional
    public ProjectResponse create(ProjectCreateRequest request) {
        projectRepository.findByProjectCode(request.projectCode())
                .ifPresent(project -> { throw new BusinessException("이미 등록된 프로젝트 ID입니다."); });

        Project project = Project.builder()
                .projectCode(request.projectCode())
                .name(request.name())
                .department(request.department())
                .requester(request.requester())
                .status(request.status())
                .openDueDate(request.openDueDate())
                .progressRate(0)
                .description(request.description())
                .build();

        Project saved = projectRepository.save(project);
        historyService.record(saved.getId(), "PROJECT", saved.getId(), "CREATE", "프로젝트가 등록되었습니다.", request.requester());
        return ProjectResponse.from(saved);
    }

    public List<ProjectResponse> search(ProjectSearchCondition condition) {
        return projectRepository.findAll(toSpecification(condition)).stream()
                .map(ProjectResponse::from)
                .toList();
    }

    public ProjectResponse get(Long projectId) {
        return ProjectResponse.from(findProject(projectId));
    }

    public Project findProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다."));
    }

    private Specification<Project> toSpecification(ProjectSearchCondition condition) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (condition.keyword() != null && !condition.keyword().isBlank()) {
                String keyword = "%" + condition.keyword().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("projectCode")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("requester")), keyword)
                ));
            }
            if (condition.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), condition.status()));
            }
            if (condition.openMonth() != null && !condition.openMonth().isBlank()) {
                YearMonth yearMonth = YearMonth.parse(condition.openMonth());
                predicates.add(criteriaBuilder.between(root.get("openDueDate"), yearMonth.atDay(1), yearMonth.atEndOfMonth()));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
