package com.library.management.controller;

import com.library.management.dto.QueueStatusDto;
import com.library.management.entity.BookQueue;
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

    // 1. Get user queue position
    @GetMapping("/status/{bookId}")
    public ResponseEntity<QueueStatusDto> getQueueStatus(@PathVariable Long bookId,
                                                         @RequestParam Long userId) {
        QueueStatusDto status = queueService.getQueueStatus(bookId, userId);
        return ResponseEntity.ok(status);
    }

    // 2. Cancel queue position
    @DeleteMapping("/cancel/{bookId}")
    public ResponseEntity<Void> cancelQueuePosition(@PathVariable Long bookId,
                                                    @RequestParam Long userId) {
        queueService.cancelQueuePosition(bookId, userId);
        return ResponseEntity.noContent().build();
    }

    // 3. Get full queue (for Admin/Owner)
    @PreAuthorize("hasAuthority('OWNER')")
    @GetMapping("/{bookId}")
    public ResponseEntity<List<QueueStatusDto>> getFullQueue(@PathVariable Long bookId) {
        List<QueueStatusDto> queue = queueService.getFullQueue(bookId);
        return ResponseEntity.ok(queue);
    }

    // 4. Reorder queue (for Admin/Owner)
    @PreAuthorize("hasAuthority('OWNER')")
    @PutMapping("/reorder/{bookId}")
    public ResponseEntity<Void> reorderQueue(@PathVariable Long bookId,
                                             @RequestBody List<Long> userIds) {
        queueService.reorderQueue(bookId, userIds);
        return ResponseEntity.noContent().build();
    }
}
