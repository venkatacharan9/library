package com.library.management.controller;

import com.library.management.dto.QueueStatusDto;
import com.library.management.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @GetMapping("/status/{bookId}")
    public ResponseEntity<QueueStatusDto> getQueueStatus(@PathVariable Long bookId,
                                                         @RequestParam Long userId) {
        QueueStatusDto status = queueService.getQueueStatus(bookId, userId);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/cancel/{bookId}")
    public ResponseEntity<Void> cancelQueuePosition(@PathVariable Long bookId,
                                                    @RequestParam Long userId) {
        queueService.cancelQueuePosition(bookId, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('OWNER')")
    @GetMapping("/{bookId}")
    public ResponseEntity<List<QueueStatusDto>> getFullQueue(@PathVariable Long bookId) {
        List<QueueStatusDto> queue = queueService.getFullQueue(bookId);
        return ResponseEntity.ok(queue);
    }

    @PreAuthorize("hasAuthority('OWNER')")
    @PutMapping("/reorder/{bookId}")
    public ResponseEntity<Void> reorderQueue(@PathVariable Long bookId,
                                             @RequestBody List<Long> userIds) {
        queueService.reorderQueue(bookId, userIds);
        return ResponseEntity.noContent().build();
    }
}
