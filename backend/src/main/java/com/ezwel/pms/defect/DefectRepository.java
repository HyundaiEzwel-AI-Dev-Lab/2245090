package com.ezwel.pms.defect;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefectRepository extends JpaRepository<Defect, Long> {
    List<Defect> findByProjectIdOrderByIdDesc(Long projectId);
    long countByProjectIdAndStatusNot(Long projectId, DefectStatus status);
}
