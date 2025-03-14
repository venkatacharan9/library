package com.library.management.scheduler;

import com.library.management.Event.BookReturnListener;
import com.library.management.entity.BookQueue;
import com.library.management.repository.BookQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookQueueRemovalService {

    private final BookQueueRepository bookQueueRepository;
    private final BookReturnListener bookReturnListener;

    @Value("${reservationDays}")
    private long reservationDays;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredQueueEntries() {
        LocalDateTime now = LocalDateTime.now();
        List<BookQueue> expiredQueueEntries = bookQueueRepository.findByReservedUntilBefore(now.minusDays(reservationDays));
        for (BookQueue expiredEntry : expiredQueueEntries) {
            bookQueueRepository.delete(expiredEntry);
        }
    }
}
