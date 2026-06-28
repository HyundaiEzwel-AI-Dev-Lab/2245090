package com.ezwel.pms.wbs;

public enum WbsStatus {
    READY("대기"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료"),
    PAUSED("일시중단");

    private final String label;

    WbsStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
