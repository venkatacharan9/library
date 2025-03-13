package com.library.management.serviceImpl;

import com.library.management.Event.BookReturnListener;
import com.library.management.Event.BookReturnedEvent;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    @Value("${library.lateFee.first10days:0}")
    private double first10DaysFee;

    @Value("${library.lateFee.next10days:2}")
    private double next10DaysFee;

    @Value("${library.lateFee.after20days:5}")
    private double after20DaysFee;

    @Value("${library.lateFee.after30days:10}")
    private double after30DaysFee;


    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    private final BookQueueRepository bookQueueRepository;
    private final BookReturnListener bookReturnListener;


    private final ApplicationEventPublisher eventPublisher;


    @Transactional(noRollbackFor = IllegalStateException.class)
    public BorrowRecord borrowBook(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BorrowRecord borrowRecord= borrowRecordRepository.findByBookIdAndUserIdAndReturnedAtIsNull(bookId,userId);

        if(borrowRecord!=null){
            throw new ResourceAlreadyExistsException("You already borrowed this book and not returned yet");
        }
        if (book.getAvailableCount() >0) {

            book.setAvailableCount(book.getAvailableCount() - 1);
            bookRepository.save(book);

            borrowRecord = new BorrowRecord();
            borrowRecord.setBook(book);
            borrowRecord.setUser(user);
            borrowRecord.setBorrowedAt(LocalDateTime.now());

            return borrowRecordRepository.save(borrowRecord);
        }
        if (!bookQueueRepository.existsByBookIdAndUserId(book.getId(), user.getId())) {
            BookQueue queueEntry = new BookQueue(null, book, user, LocalDateTime.now());
            bookQueueRepository.save(queueEntry);
            bookQueueRepository.flush();
            throw new IllegalStateException("No copies available. You have been added to the queue.");
        } else {
            throw new IllegalStateException("You are already in the queue for this book.");
        }

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

         bookReturnListener.sendQueueNotification(nextUser.getUser().getEmail(), book.getTitle());

            bookQueueRepository.delete(nextUser);
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
