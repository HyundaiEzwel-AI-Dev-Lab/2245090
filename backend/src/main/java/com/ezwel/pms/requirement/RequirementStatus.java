package com.ezwel.pms.requirement;

public enum RequirementStatus {
    DRAFT("작성중"),
    CONFIRMED("확정"),
    REJECTED("반려");

    private final String label;

    RequirementStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
