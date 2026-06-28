package com.ezwel.pms.history;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final ChangeHistoryRepository changeHistoryRepository;

    public void record(Long projectId, String targetType, Long targetId, String changeType, String message, String changedBy) {
        changeHistoryRepository.save(ChangeHistory.builder()
                .projectId(projectId)
                .targetType(targetType)
                .targetId(targetId)
                .changeType(changeType)
                .message(message)
                .changedBy(changedBy)
                .build());
    }
}
