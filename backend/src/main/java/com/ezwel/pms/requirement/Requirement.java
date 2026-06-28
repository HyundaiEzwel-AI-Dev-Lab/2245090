package com.ezwel.pms.requirement;

import com.ezwel.pms.common.BaseEntity;
import com.ezwel.pms.project.Project;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "requirements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Requirement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "system_type", nullable = false, length = 50)
    private String systemType;

    @Column(name = "business_type", nullable = false, length = 50)
    private String businessType;

    @Column(name = "menu_name", nullable = false, length = 200)
    private String menuName;

    @Column(name = "description", length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private RequirementStatus status;

    public void confirm() {
        this.status = RequirementStatus.CONFIRMED;
    }

    public boolean isConfirmed() {
        return RequirementStatus.CONFIRMED.equals(this.status);
    }
}
