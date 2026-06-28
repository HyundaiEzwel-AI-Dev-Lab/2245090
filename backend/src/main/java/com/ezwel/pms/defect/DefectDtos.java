package com.ezwel.pms.defect;

public final class DefectDtos {
    private DefectDtos() {}

    public record DefectResponse(
            Long id,
            Long projectId,
            Long testCaseId,
            String title,
            String description,
            String assignee,
            DefectStatus status,
            String statusLabel,
            String resolution
    ) {
        public static DefectResponse from(Defect defect) {
            return new DefectResponse(
                    defect.getId(),
                    defect.getProject().getId(),
                    defect.getTestCase() == null ? null : defect.getTestCase().getId(),
                    defect.getTitle(),
                    defect.getDescription(),
                    defect.getAssignee(),
                    defect.getStatus(),
                    defect.getStatus().getLabel(),
                    defect.getResolution()
            );
        }
    }
}
