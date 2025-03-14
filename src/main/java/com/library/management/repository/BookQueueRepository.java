package com.library.management.repository;

import com.library.management.entity.BookQueue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookQueueRepository extends JpaRepository<BookQueue,Long> {

    boolean existsByBookIdAndUserId(Long bookId,Long userId);

    Optional<BookQueue> findFirstByBookIdOrderByQueuedAtAsc(Long bookId);

    List<BookQueue> findByBookIdOrderByQueuedAtAsc(Long bookId);

    Optional<BookQueue> findByBookIdAndUserId(Long bookId, Long userId);

    List<BookQueue> findByReservedUntilBefore(LocalDateTime localDateTime);

    List<BookQueue> findFirstNByBookIdOrderByQueuedAtAsc(Long bookId, Pageable limit);

}
