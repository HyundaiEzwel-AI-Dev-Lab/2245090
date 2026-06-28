package com.ezwel.pms.wbs;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public final class WbsDtos {
    private WbsDtos() {}

    public record ScheduleUpdateRequest(
            @NotNull LocalDate planStartDate,
            @NotNull LocalDate planEndDate,
            String note
    ) {}

    public record WbsTaskResponse(
            Long id,
            Long projectId,
            Long requirementId,
            String taskName,
            WorkType workType,
            String workTypeLabel,
            String assignee,
            LocalDate planStartDate,
            LocalDate planEndDate,
            LocalDate actualStartDate,
            LocalDate actualEndDate,
            int progressRate,
            WbsStatus status,
            String statusLabel,
            boolean delayed,
            String priority,
            String difficulty,
            boolean unitTestEnabled,
            String note
    ) {
        public static WbsTaskResponse from(WbsTask task, LocalDate today) {
            return new WbsTaskResponse(
                    task.getId(),
                    task.getProject().getId(),
                    task.getRequirement() == null ? null : task.getRequirement().getId(),
                    task.getTaskName(),
                    task.getWorkType(),
                    task.getWorkType().getLabel(),
                    task.getAssignee(),
                    task.getPlanStartDate(),
                    task.getPlanEndDate(),
                    task.getActualStartDate(),
                    task.getActualEndDate(),
                    task.getProgressRate(),
                    task.getStatus(),
                    task.getStatus().getLabel(),
                    task.isDelayed(today),
                    task.getPriority(),
                    task.getDifficulty(),
                    task.isUnitTestEnabled(),
                    task.getNote()
            );
        }
    }
}
