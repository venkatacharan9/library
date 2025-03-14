package com.library.management.service;

import com.library.management.entity.BorrowRecord;
import org.springframework.data.domain.Page;

public interface BorrowService {

         BorrowRecord borrowBook(Long bookId, Long userId);
         BorrowRecord returnBook(Long borrowId);
         Page<BorrowRecord> getBookHistory(Long bookId, int page, int size);
         Page<BorrowRecord> borrowedBooks(Long userId, int page, int size);
}
