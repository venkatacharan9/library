package com.library.management.service;

import com.library.management.entity.BorrowRecord;
import org.springframework.data.domain.Page;

public interface BorrowService {

        public BorrowRecord borrowBook(Long bookId, Long userId);
        public BorrowRecord returnBook(Long borrowId);
        public Page<BorrowRecord> getBookHistory(Long bookId, int page, int size);
        public Page<BorrowRecord> borrowedBooks(Long userId, int page, int size);
}
