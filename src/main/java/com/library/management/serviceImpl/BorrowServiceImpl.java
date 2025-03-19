package com.library.management.serviceImpl;

import com.library.management.Event.BookReturnedEvent;
import com.library.management.Event.EmailNotificationService;
import com.library.management.entity.Book;
import com.library.management.entity.BookQueue;
import com.library.management.entity.BorrowRecord;
import com.library.management.entity.User;
import com.library.management.exception.ResourceAlreadyExistsException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BookQueueRepository;
import com.library.management.repository.BookRepository;
import com.library.management.repository.BorrowRecordRepository;
import com.library.management.repository.UserRepository;
import com.library.management.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class  BorrowServiceImpl implements BorrowService {

    @Value("${library.lateFee.first10days:0}")
    private double first10DaysFee;

    @Value("${library.lateFee.next10days}")
    private double next10DaysFee;

    @Value("${library.lateFee.after20days}")
    private double after20DaysFee;

    @Value("${library.lateFee.after30days}")
    private double after30DaysFee;

    @Value("${reservationDays}")
    private Long reservationDays;


    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    private final BookQueueRepository bookQueueRepository;
    private final EmailNotificationService emailNotificationService;



    private final ApplicationEventPublisher eventPublisher;

    @Transactional(noRollbackFor = IllegalStateException.class)
    public BorrowRecord borrowBook(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BorrowRecord existingBorrow = borrowRecordRepository.findByBookIdAndUserIdAndReturnedAtIsNull(bookId, userId);
        if (existingBorrow != null) {
            throw new ResourceAlreadyExistsException("You already borrowed this book and not returned yet.");
        }
        List<BookQueue> bookQueues = bookQueueRepository.findByBookIdOrderByQueuedAtAsc(bookId);
        if (bookQueues.size() >= book.getAvailableCount()) {
            boolean userEligible = isUserEligibleForBorrow(bookQueues, user.getId(), book.getAvailableCount());
            if (!userEligible) {
                if (!bookQueueRepository.existsByBookIdAndUserId(book.getId(), user.getId())) {
                    BookQueue queueEntry = new BookQueue(null, book, user, LocalDateTime.now(), LocalDateTime.now().plusDays(reservationDays),false);
                    bookQueueRepository.save(queueEntry);
                    throw new IllegalStateException("No copies available. You have been added to the queue.");
                } else {
                    throw new IllegalStateException("You are already in the queue for this book.");
                }
            } else {
                return processBorrowFromQueue(book, user);
            }
        } else {
            return borrowBookFromAvailable(book, user);
        }
    }

    private boolean isUserEligibleForBorrow(List<BookQueue> bookQueues, Long userId, int availableCount) {
        for (int i = 0; i < availableCount; i++) {
            BookQueue queueEntry = bookQueues.get(i);
            if (queueEntry.getUser().getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }


    private BorrowRecord processBorrowFromQueue(Book book, User user) {
        BorrowRecord borrowRecord = this.borrowBookFromAvailable(book, user);
        BookQueue userQueue = bookQueueRepository.findByBookIdAndUserId(book.getId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found in queue"));
        bookQueueRepository.delete(userQueue);
        return borrowRecord;
    }

    private BorrowRecord borrowBookFromAvailable(Book book, User user) {
        book = bookRepository.findByIdAndLock(book.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        book.setAvailableCount(book.getAvailableCount() - 1);
        bookRepository.save(book);

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBook(book);
        borrowRecord.setUser(user);
        borrowRecord.setBorrowedAt(LocalDateTime.now());
        return borrowRecordRepository.save(borrowRecord);
    }

    @Transactional
    public BorrowRecord returnBook(Long borrowId) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found"));

        if (borrowRecord.getReturnedAt() != null) {
            throw new IllegalStateException("Book already returned.");
        }

        long daysBorrowed = ChronoUnit.DAYS.between(borrowRecord.getBorrowedAt(), LocalDateTime.now());
        double lateFee = calculateLateFee(daysBorrowed);

        borrowRecord.returnBook(lateFee);
        borrowRecordRepository.save(borrowRecord);

        Book book = borrowRecord.getBook();
        book.setAvailableCount(book.getAvailableCount() + 1);
        bookRepository.save(book);

        eventPublisher.publishEvent(new BookReturnedEvent(this, borrowRecord));

        Optional<BookQueue> nextUserQueue = bookQueueRepository.findFirstByBookIdOrderByQueuedAtAsc(book.getId());
        if (nextUserQueue.isPresent()) {
            BookQueue nextUser = nextUserQueue.get();
            emailNotificationService.sendQueueNotification(nextUser.getUser().getEmail(), book.getTitle());
        }

        return borrowRecord;
    }

    private double calculateLateFee(long daysBorrowed) {
        double fee =first10DaysFee;

        if (daysBorrowed > 10) {
            long lateDays = daysBorrowed - 10;

            if (lateDays <= 10) {
                fee = lateDays * next10DaysFee;
            } else if (lateDays <= 20) {
                fee = (10 * next10DaysFee) + ((lateDays - 10) * after20DaysFee);
            } else {
                fee = (10 * next10DaysFee) + (10 * after20DaysFee) + ((lateDays - 20) * after30DaysFee);
            }
        }
        return fee;
    }

    public Page<BorrowRecord> getBookHistory(Long bookId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        if (bookId != null) {
            return borrowRecordRepository.findByBookId(bookId, pageable);
        } else {
            return borrowRecordRepository.findAll(pageable);
        }
    }

    @Override
    public Page<BorrowRecord> borrowedBooks(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        if (userId != null) {
            return borrowRecordRepository.findByUserId(userId, pageable);
        } else {
            return borrowRecordRepository.findAll(pageable);
        }

    }

}
