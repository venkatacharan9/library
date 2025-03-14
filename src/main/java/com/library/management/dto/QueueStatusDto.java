package com.library.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
public class QueueStatusDto {
    private Long userId;
    private String userName;
    private LocalDateTime queuedAt;
    private int totalInQueue;
    private int position;



    public QueueStatusDto(Long userId,String userName,int position, int totalInQueue) {
        this.userId = userId;
        this.userName = userName;
        this.position = position;
        this.totalInQueue = totalInQueue;
    }

    public QueueStatusDto(Long id, String name, LocalDateTime queuedAt) {
        this.userId = id;
        this.userName = name;
        this.queuedAt = queuedAt;
    }
}
