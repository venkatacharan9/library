package com.library.management;

import com.library.management.dto.QueueStatusDto;
import com.library.management.entity.Book;
import com.library.management.entity.BookQueue;
import com.library.management.entity.User;
import com.library.management.repository.BookQueueRepository;
import com.library.management.repository.BookRepository;
import com.library.management.repository.UserRepository;
import com.library.management.service.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class QueueServiceImplTest {

    @Autowired
    private QueueService queueService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookQueueRepository bookQueueRepository;

    private Book testBook;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setup() {
        bookQueueRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setAvailableCount(0);
        testBook.setTotalCount(5);
        testBook.setCreatedBy("Test");
        testBook.setCreatedDate(LocalDateTime.now());
        bookRepository.save(testBook);

        user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setName("user1");
        userRepository.save(user1);

        user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setName("user2");
        user2.setPassword("password2");
        userRepository.save(user2);

        user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setName("user3");
        user3.setPassword("password3");
        userRepository.save(user3);

        bookQueueRepository.save(new BookQueue(null, testBook, user1, LocalDateTime.now(),LocalDateTime.now().plusDays(2),false));
        bookQueueRepository.save(new BookQueue(null, testBook, user2, LocalDateTime.now().plusSeconds(10),LocalDateTime.now().plusDays(3),false));
        bookQueueRepository.save(new BookQueue(null, testBook, user3, LocalDateTime.now().plusSeconds(20),LocalDateTime.now().plusDays(3),false));
    }

    @Test
    public void testCancelQueuePosition() {
        queueService.cancelQueuePosition(testBook.getId(), user2.getId());

        List<BookQueue> remainingQueue = bookQueueRepository.findByBookIdOrderByQueuedAtAsc(testBook.getId());
        assertThat(remainingQueue).hasSize(2);
        assertThat(remainingQueue.stream().anyMatch(q -> q.getUser().getId().equals(user2.getId()))).isFalse();
    }

    @Test
    public void testGetFullQueue() {
        List<QueueStatusDto> queue = queueService.getFullQueue(testBook.getId());

        assertThat(queue).hasSize(3);
        assertThat(queue.get(0).getUserId()).isEqualTo(user1.getId());
        assertThat(queue.get(1).getUserId()).isEqualTo(user2.getId());
        assertThat(queue.get(2).getUserId()).isEqualTo(user3.getId());
    }

    @Test
    public void testReorderQueue() {
        List<Long> newOrder = Arrays.asList(user3.getId(), user1.getId(), user2.getId());

        queueService.reorderQueue(testBook.getId(), newOrder);

        List<BookQueue> reorderedQueue = bookQueueRepository.findByBookIdOrderByQueuedAtAsc(testBook.getId());

        assertThat(reorderedQueue).hasSize(3);
        assertThat(reorderedQueue.get(0).getUser().getId()).isEqualTo(user3.getId());
        assertThat(reorderedQueue.get(1).getUser().getId()).isEqualTo(user1.getId());
        assertThat(reorderedQueue.get(2).getUser().getId()).isEqualTo(user2.getId());
    }

    @Test
    public void testGetFullQueueWhenEmpty() {
        bookQueueRepository.deleteAll();

        List<QueueStatusDto> queue = queueService.getFullQueue(testBook.getId());

        assertThat(queue).isEmpty();
    }
}
