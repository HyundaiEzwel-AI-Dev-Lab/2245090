package com.ezwel.pms.defect;

public enum DefectStatus {
    OPEN("접수"),
    IN_PROGRESS("조치중"),
    RESOLVED("조치완료"),
    CLOSED("종료");

    private final String label;

    DefectStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
