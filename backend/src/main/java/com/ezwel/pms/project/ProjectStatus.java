package com.ezwel.pms.project;

public enum ProjectStatus {
    RECEIVED("접수"),
    DISCUSSING("협의중"),
    IN_PROGRESS("처리중"),
    TESTING("테스트"),
    COMPLETED("완료"),
    CANCELED("취소");

    private final String label;

    ProjectStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
