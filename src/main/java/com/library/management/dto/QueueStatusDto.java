package com.library.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class QueueStatusDto {
    private Long userId;
    private String userName;
    private LocalDateTime queuedAt;

    public QueueStatusDto(int position, int totalInQueue) {
        this.userId = null;
        this.userName = "Position " + position + " of " + totalInQueue;
        this.queuedAt = null;
    }
}
