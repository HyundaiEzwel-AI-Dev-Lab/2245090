package com.ezwel.pms.test;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByProjectIdOrderByIdDesc(Long projectId);
    List<TestCase> findByProjectIdAndStageOrderByIdDesc(Long projectId, TestStage stage);
}
