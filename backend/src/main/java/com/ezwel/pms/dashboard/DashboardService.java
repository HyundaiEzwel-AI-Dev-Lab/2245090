package com.ezwel.pms.dashboard;

import com.ezwel.pms.project.ProjectRepository;
import com.ezwel.pms.project.ProjectStatus;
import com.ezwel.pms.wbs.WbsTaskRepository;
import com.ezwel.pms.wbs.WbsStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private final ProjectRepository projectRepository;
    private final WbsTaskRepository wbsTaskRepository;

    public DashboardSummary getSummary() {
        long total = projectRepository.count();
        long received = projectRepository.findAll().stream().filter(project -> project.getStatus() == ProjectStatus.RECEIVED).count();
        long progressing = projectRepository.findAll().stream().filter(project -> project.getStatus() == ProjectStatus.DISCUSSING || project.getStatus() == ProjectStatus.IN_PROGRESS || project.getStatus() == ProjectStatus.TESTING).count();
        long completed = projectRepository.findAll().stream().filter(project -> project.getStatus() == ProjectStatus.COMPLETED).count();
        long delayed = wbsTaskRepository.findAll().stream()
                .filter(task -> task.getPlanEndDate() != null)
                .filter(task -> task.getStatus() != WbsStatus.COMPLETED)
                .filter(task -> LocalDate.now().isAfter(task.getPlanEndDate()))
                .count();
        return new DashboardSummary(total, received, progressing, completed, delayed);
    }

    public record DashboardSummary(long totalProjects, long receivedProjects, long progressingProjects, long completedProjects, long delayedTasks) {}
}
