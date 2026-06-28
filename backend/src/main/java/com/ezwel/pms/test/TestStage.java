package com.ezwel.pms.test;

public enum TestStage {
    UNIT("단위테스트"),
    DEV("DEV테스트"),
    OPERATION("운영테스트");

    private final String label;

    TestStage(String label) { this.label = label; }

    public String getLabel() { return label; }
}
