package com.library.management.controller;

import com.library.management.entity.BorrowRecord;
import com.library.management.service.BookService;
import com.library.management.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping("borrow/{bookId}")
    public ResponseEntity<BorrowRecord> borrowBook(
            @PathVariable Long bookId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(borrowService.borrowBook(bookId, userId));
    }

    @PostMapping("/return/{borrowId}")
    public ResponseEntity<BorrowRecord> returnBook(@PathVariable Long borrowId) {
        return ResponseEntity.ok(borrowService.returnBook(borrowId));
    }

    @GetMapping("/borrowedBooks")
    public ResponseEntity<Page<BorrowRecord>> borrowedBooks(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(borrowService.borrowedBooks(userId, page, size));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<Page<BorrowRecord>> getBorrowHistory(
            @RequestParam(required = false) Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(borrowService.getBookHistory(bookId, page, size));
    }
}
