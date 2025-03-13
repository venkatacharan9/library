package com.library.management.service;

import com.library.management.dto.QueueStatusDto;

import java.util.List;

public interface QueueService {
    QueueStatusDto getQueueStatus(Long bookId, Long userId);
    void cancelQueuePosition(Long bookId, Long userId);
    List<QueueStatusDto> getFullQueue(Long bookId);
    void reorderQueue(Long bookId, List<Long> userIds);
}
