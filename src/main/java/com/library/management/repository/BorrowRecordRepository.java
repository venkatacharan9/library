package com.library.management.repository;

import com.library.management.entity.BorrowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord,Long> {

    Page<BorrowRecord> findByBookId(Long bookId, Pageable page);

    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);

    BorrowRecord findByBookIdAndUserIdAndReturnedAtIsNull(Long bookId, Long userId);
}
