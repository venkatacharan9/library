package com.library.management.serviceImpl;

import com.library.management.dto.QueueStatusDto;
import com.library.management.entity.BookQueue;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BookQueueRepository;
import com.library.management.repository.UserRepository;
import com.library.management.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {

    private final BookQueueRepository bookQueueRepository;
    private final UserRepository userRepository;

    @Override
    public QueueStatusDto getQueueStatus(Long bookId, Long userId) {
        List<BookQueue> queue = bookQueueRepository.findByBookIdOrderByQueuedAtAsc(bookId);

        int position = 1;
        for (BookQueue entry : queue) {
            if (entry.getUser().getId().equals(userId)) {
                return new QueueStatusDto(entry.getUser().getId(),entry.getUser().getName(),position, queue.size());
            }
            position++;
        }
        throw new ResourceNotFoundException("You are not in the queue for this book.");
    }

    @Override
    @Transactional
    public void cancelQueuePosition(Long bookId, Long userId) {
        BookQueue queueEntry = bookQueueRepository.findByBookIdAndUserId(bookId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found"));

        bookQueueRepository.delete(queueEntry);
    }

    @Override
    public List<QueueStatusDto> getFullQueue(Long bookId) {
        return bookQueueRepository.findByBookIdOrderByQueuedAtAsc(bookId).stream()
                .map(entry -> new QueueStatusDto(
                        entry.getUser().getId(),
                        entry.getUser().getName(),
                        entry.getQueuedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void reorderQueue(Long bookId, List<Long> userIds) {
        List<BookQueue> queue = bookQueueRepository.findByBookIdOrderByQueuedAtAsc(bookId);

        if (queue.size() != userIds.size()) {
            throw new IllegalStateException("Provided userIds size does not match queue size");
        }
        for (int i = 0; i < userIds.size(); i++) {
            Long userId = userIds.get(i);

            BookQueue entry = queue.stream()
                    .filter(q -> q.getUser().getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("User not found in queue"));

            entry.setQueuedAt(queue.get(0).getQueuedAt().plusSeconds(i));
        }

        bookQueueRepository.saveAll(queue);
    }

}
