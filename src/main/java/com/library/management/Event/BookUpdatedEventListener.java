package com.library.management.Event;

import com.library.management.entity.BookQueue;
import com.library.management.repository.BookQueueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class BookUpdatedEventListener {

    @Autowired
    private BookQueueRepository bookQueueRepository;

    @Autowired
    private EmailNotificationService emailNotificationService;

    @EventListener
    @Async
    public void handleBookUpdatedEvent(BookUpdatedEvent event) {

        List<BookQueue> queuedUsers = bookQueueRepository
                .findTopNByBookIdAndNotifiedFalseOrderByQueuedAtAsc(event.getBookId(), event.getAvailableCount());

        for (BookQueue queueEntry : queuedUsers)
        {
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
