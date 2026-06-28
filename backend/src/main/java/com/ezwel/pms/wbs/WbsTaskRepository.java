package com.ezwel.pms.wbs;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WbsTaskRepository extends JpaRepository<WbsTask, Long> {
    List<WbsTask> findByProjectIdOrderByIdDesc(Long projectId);
    List<WbsTask> findByAssigneeOrderByPlanEndDateAsc(String assignee);
    boolean existsByRequirementId(Long requirementId);
}
