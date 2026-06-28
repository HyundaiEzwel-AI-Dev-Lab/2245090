package com.ezwel.pms.wbs;

public enum WorkType {
    PLANNING("기획"),
    DESIGN("디자인"),
    PUBLISHING("퍼블리싱"),
    DEVELOPMENT("개발"),
    UNIT_TEST("단위테스트"),
    DEV_TEST("DEV테스트"),
    OPERATION_TEST("운영테스트");

    private final String label;

    WorkType(String label) { this.label = label; }

    public String getLabel() { return label; }
}
