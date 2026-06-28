package com.ezwel.pms.defect;

import com.ezwel.pms.common.BaseEntity;
import com.ezwel.pms.project.Project;
import com.ezwel.pms.test.TestCase;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "defects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Defect extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id")
    private TestCase testCase;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "assignee", nullable = false, length = 50)
    private String assignee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DefectStatus status;

    @Column(name = "resolution", length = 2000)
    private String resolution;

    public void resolve(String resolution) {
        this.resolution = resolution;
        this.status = DefectStatus.RESOLVED;
    }
}
