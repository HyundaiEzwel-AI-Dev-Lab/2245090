package com.ezwel.pms.test;

import com.ezwel.pms.common.BaseEntity;
import com.ezwel.pms.project.Project;
import com.ezwel.pms.wbs.WbsTask;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_cases")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TestCase extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wbs_task_id")
    private WbsTask wbsTask;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false, length = 30)
    private TestStage stage;

    @Column(name = "case_name", nullable = false, length = 200)
    private String caseName;

    @Column(name = "procedure", nullable = false, length = 1000)
    private String procedure;

    @Column(name = "expected_result", nullable = false, length = 1000)
    private String expectedResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 30)
    private TestResult result;

    @Column(name = "tester", length = 50)
    private String tester;

    @Column(name = "actual_result", length = 1000)
    private String actualResult;

    public void execute(TestResult result, String actualResult, String tester) {
        this.result = result;
        this.actualResult = actualResult;
        this.tester = tester;
    }

    public boolean isFailed() {
        return TestResult.FAIL.equals(this.result);
    }
}
