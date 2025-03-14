package com.library.management;

import com.library.management.entity.Book;
import com.library.management.entity.BorrowRecord;
import com.library.management.entity.User;
import com.library.management.repository.BookRepository;
import com.library.management.repository.BorrowRecordRepository;
import com.library.management.repository.UserRepository;
import com.library.management.service.BorrowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BorrowServiceImplTest {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private UserRepository userRepository;

    private Book testBook;
    private User testUser;

    @BeforeEach
    public void setup() {
        borrowRecordRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setAvailableCount(5);
        testBook.setTotalCount(10);
        testBook.setCreatedBy("Test");
        bookRepository.save(testBook);

        testUser = new User();
        testUser.setEmail("testuser@example.com");
        testUser.setName("test");
        testUser.setPassword("randomKey");
        userRepository.save(testUser);
    }

    @Test
    public void testBorrowBook() {
        BorrowRecord borrowRecord = borrowService.borrowBook(testBook.getId(), testUser.getId());

        assertThat(borrowRecord).isNotNull();
        assertThat(borrowRecord.getUser()).isEqualTo(testUser);
        assertThat(borrowRecord.getBorrowedAt()).isNotNull();

        Book updatedBook = bookRepository.findById(testBook.getId()).get();
        assertThat(updatedBook.getAvailableCount()).isEqualTo(4);
    }

    @Test
    public void testReturnBookAndLateFee() {
        BorrowRecord borrowRecord = borrowService.borrowBook(testBook.getId(), testUser.getId());

        borrowRecord.setBorrowedAt(LocalDateTime.now().minusDays(15));

        BorrowRecord returnedRecord = borrowService.returnBook(borrowRecord.getId());

        assertThat(returnedRecord).isNotNull();
        assertThat(returnedRecord.getReturnedAt()).isNotNull();
        assertThat(returnedRecord.getLateFee()).isEqualTo(0);

        Book updatedBook = bookRepository.findById(testBook.getId()).get();
        assertThat(updatedBook.getAvailableCount()).isEqualTo(5);

    }

    @Test
    public void testQueueManagementWhenBookReturned() {
        BorrowRecord borrowRecord = borrowService.borrowBook(testBook.getId(), testUser.getId());

        User anotherUser = new User();
        anotherUser.setEmail("anotheruser@example.com");
        anotherUser.setName("anotheruser");
        anotherUser.setPassword("randomKey");
        userRepository.save(anotherUser);

        borrowService.borrowBook(testBook.getId(), anotherUser.getId());  // User gets added to the queue

        borrowRecord.setBorrowedAt(LocalDateTime.now().minusDays(15));
        borrowRecord.setReturnedAt(LocalDateTime.now());
        borrowRecordRepository.save(borrowRecord);

    }
}
