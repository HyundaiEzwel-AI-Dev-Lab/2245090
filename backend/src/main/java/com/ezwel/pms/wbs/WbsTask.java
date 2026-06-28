package com.ezwel.pms.wbs;

import com.ezwel.pms.common.BaseEntity;
import com.ezwel.pms.project.Project;
import com.ezwel.pms.requirement.Requirement;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "wbs_tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WbsTask extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id")
    private Requirement requirement;

    @Column(name = "task_name", nullable = false, length = 200)
    private String taskName;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", nullable = false, length = 30)
    private WorkType workType;

    @Column(name = "assignee", nullable = false, length = 50)
    private String assignee;

    @Column(name = "plan_start_date")
    private LocalDate planStartDate;

    @Column(name = "plan_end_date")
    private LocalDate planEndDate;

    @Column(name = "actual_start_date")
    private LocalDate actualStartDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @Column(name = "progress_rate", nullable = false)
    private int progressRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private WbsStatus status;

    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    @Column(name = "difficulty", nullable = false, length = 20)
    private String difficulty;

    @Column(name = "unit_test_enabled", nullable = false)
    private boolean unitTestEnabled;

    @Column(name = "note", length = 500)
    private String note;

    public void updateSchedule(LocalDate planStartDate, LocalDate planEndDate, String note) {
        this.planStartDate = planStartDate;
        this.planEndDate = planEndDate;
        this.note = note;
    }

    public void start(LocalDate actualStartDate) {
        this.actualStartDate = actualStartDate;
        this.status = WbsStatus.IN_PROGRESS;
        if (this.progressRate == 0) {
            this.progressRate = 10;
        }
    }

    public void complete(LocalDate actualEndDate) {
        this.actualEndDate = actualEndDate;
        this.status = WbsStatus.COMPLETED;
        this.progressRate = 100;
    }

    public boolean isDelayed(LocalDate today) {
        return planEndDate != null && actualEndDate == null && today.isAfter(planEndDate);
    }
}
