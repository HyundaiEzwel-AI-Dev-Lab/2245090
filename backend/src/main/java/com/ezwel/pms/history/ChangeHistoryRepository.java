package com.ezwel.pms.history;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeHistoryRepository extends JpaRepository<ChangeHistory, Long> {
    List<ChangeHistory> findByProjectIdOrderByIdDesc(Long projectId);
}
