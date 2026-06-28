package com.ezwel.pms.project;

import com.ezwel.pms.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Project extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_code", nullable = false, unique = true, length = 30)
    private String projectCode;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "requester", nullable = false, length = 50)
    private String requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ProjectStatus status;

    @Column(name = "open_due_date")
    private LocalDate openDueDate;

    @Column(name = "progress_rate", nullable = false)
    private int progressRate;

    @Column(name = "description", length = 2000)
    private String description;

    public void updateStatus(ProjectStatus status) {
        this.status = status;
    }

    public void updateProgressRate(int progressRate) {
        this.progressRate = Math.max(0, Math.min(100, progressRate));
    }
}
