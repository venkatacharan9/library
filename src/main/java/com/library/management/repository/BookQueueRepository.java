package com.library.management.repository;

import com.library.management.entity.BookQueue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookQueueRepository extends JpaRepository<BookQueue,Long> {

    boolean existsByBookIdAndUserId(Long bookId,Long userId);

    Optional<BookQueue> findFirstByBookIdAndNotifiedFalseOrderByQueuedAtAsc(Long bookId);

    List<BookQueue> findByBookIdOrderByQueuedAtAsc(Long bookId);

    Optional<BookQueue> findByBookIdAndUserId(Long bookId, Long userId);

    List<BookQueue> findByReservedUntilBefore(LocalDateTime localDateTime);

    @Query(value = "SELECT * FROM book_queue WHERE book_id = :bookId AND notified = false ORDER BY queued_at ASC LIMIT :limit", nativeQuery = true)
    List<BookQueue> findTopNByBookIdAndNotifiedFalseOrderByQueuedAtAsc(@Param("bookId") Long bookId, @Param("limit") int limit);

}
