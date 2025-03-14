package com.library.management.Event;

import com.library.management.entity.BorrowRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookReturnListener {

    private final EmailNotificationService emailNotificationService;

    @EventListener
    public void handleBookReturned(BookReturnedEvent event) {
        BorrowRecord record = event.getBorrowRecord();

        String userEmail = record.getUser().getEmail();
        String bookTitle = record.getBook().getTitle();
        double lateFee = record.getLateFee();

        log.info("Book '{}' returned by user '{}'. Late Fee: {} DKK",
                bookTitle, record.getUser().getEmail(), lateFee);

        emailNotificationService.sendReturnEmail(userEmail, bookTitle, lateFee);
    }

}
