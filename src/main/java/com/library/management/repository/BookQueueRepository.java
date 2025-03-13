package com.library.management.repository;

import com.library.management.entity.BookQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookQueueRepository extends JpaRepository<BookQueue,Long> {

    boolean existsByBookIdAndUserId(Long bookId,Long userId);

    Optional<BookQueue> findFirstByBookIdOrderByQueuedAtAsc(Long bookId);
}
