package com.ezwel.pms.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public final class ProjectDtos {
    private ProjectDtos() {}

    public record ProjectCreateRequest(
            @NotBlank String projectCode,
            @NotBlank String name,
            @NotBlank String department,
            @NotBlank String requester,
            @NotNull ProjectStatus status,
            LocalDate openDueDate,
            String description
    ) {}

    public record ProjectSearchCondition(
            String keyword,
            ProjectStatus status,
            String openMonth
    ) {}

    public record ProjectResponse(
            Long id,
            String projectCode,
            String name,
            String department,
            String requester,
            ProjectStatus status,
            String statusLabel,
            LocalDate openDueDate,
            int progressRate,
            String description
    ) {
        public static ProjectResponse from(Project project) {
            return new ProjectResponse(
                    project.getId(),
                    project.getProjectCode(),
                    project.getName(),
                    project.getDepartment(),
                    project.getRequester(),
                    project.getStatus(),
                    project.getStatus().getLabel(),
                    project.getOpenDueDate(),
                    project.getProgressRate(),
                    project.getDescription()
            );
        }
    }
}
