package com.library.management.scheduler;

import com.library.management.Event.EmailNotificationService;
import com.library.management.entity.BookQueue;
import com.library.management.repository.BookQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookQueueNotifyService {

    private final BookQueueRepository bookQueueRepository;
    private final EmailNotificationService emailNotificationService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void processQueueNotifications() {
        List<BookQueue> queuedUsers = bookQueueRepository.findPendingNotifications();
        for (BookQueue queueEntry : queuedUsers) {
            try {
                emailNotificationService.sendQueueNotification(
                        queueEntry.getUser().getEmail(),
                        queueEntry.getBook().getTitle()
                );
                queueEntry.setNotified(true);
                bookQueueRepository.save(queueEntry);
            } catch (Exception e) {
                log.error("Failed to send notification to user: {}", queueEntry.getUser().getEmail(), e);
            }
        }
    }
}
