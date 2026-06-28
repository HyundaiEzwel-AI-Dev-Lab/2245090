package com.ezwel.pms.history;

import com.ezwel.pms.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "change_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChangeHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "change_type", nullable = false, length = 50)
    private String changeType;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "changed_by", nullable = false, length = 50)
    private String changedBy;
}
