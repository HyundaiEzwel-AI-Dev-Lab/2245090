package com.ezwel.pms.test;

public enum TestResult {
    NOT_RUN("미수행"),
    PASS("정상"),
    FAIL("오류");

    private final String label;

    TestResult(String label) { this.label = label; }

    public String getLabel() { return label; }
}
