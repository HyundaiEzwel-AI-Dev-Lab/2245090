package com.ezwel.pms.user;

import com.ezwel.pms.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pms_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PmsUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_no", nullable = false, unique = true, length = 30)
    private String employeeNo;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "role", nullable = false, length = 30)
    private String role;

    @Column(name = "active", nullable = false)
    private boolean active;
}
