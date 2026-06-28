package com.ezwel.pms.requirement;

import jakarta.validation.constraints.NotBlank;

public final class RequirementDtos {
    private RequirementDtos() {}

    public record RequirementCreateRequest(
            @NotBlank String title,
            @NotBlank String systemType,
            @NotBlank String businessType,
            @NotBlank String menuName,
            String description
    ) {}

    public record RequirementResponse(
            Long id,
            Long projectId,
            String title,
            String systemType,
            String businessType,
            String menuName,
            String description,
            RequirementStatus status,
            String statusLabel
    ) {
        public static RequirementResponse from(Requirement requirement) {
            return new RequirementResponse(
                    requirement.getId(),
                    requirement.getProject().getId(),
                    requirement.getTitle(),
                    requirement.getSystemType(),
                    requirement.getBusinessType(),
                    requirement.getMenuName(),
                    requirement.getDescription(),
                    requirement.getStatus(),
                    requirement.getStatus().getLabel()
            );
        }
    }
}
