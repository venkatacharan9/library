package com.library.management.serviceImpl;
import com.library.management.Event.BookUpdatedEvent;
import com.library.management.Event.EmailNotificationService;
import com.library.management.dto.BookDto;
import com.library.management.entity.Book;
import com.library.management.entity.BookQueue;
import com.library.management.exception.ResourceAlreadyExistsException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BookQueueRepository;
import com.library.management.repository.BookRepository;
import com.library.management.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookQueueRepository bookQueueRepository;
    private final EmailNotificationService emailNotificationService;
    private final ApplicationEventPublisher eventPublisher;



    public Page<Book> getAllBooks(int page, int size) {

        return bookRepository.findAll(PageRequest.of(page, size));
    }

    @Cacheable(value = "books", key = "#id")
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found."));
    }

    public Page<Book> searchBooks(String title, String author,String anyType, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Book> books = bookRepository.searchBooks(title, author,anyType, pageable);
        if (books.isEmpty()) {
            throw new ResourceNotFoundException("No books found matching the search criteria.");
        }
        return books;
    }

    @Transactional
    public Book addBook(BookDto bookDto) {
        if (bookRepository.existsByTitleAndAuthor(bookDto.getTitle(), bookDto.getAuthor())) {
            throw new ResourceAlreadyExistsException("Book with title '" + bookDto.getTitle() + "' by " + bookDto.getAuthor() + " already exists.");
        }
        Book book= new Book();
        BeanUtils.copyProperties(bookDto,book);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null) book.setCreatedBy(authentication.getName());
        book.setCreatedDate(LocalDateTime.now());
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, BookDto updatedBook) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found."));

        int oldTotalCount = book.getTotalCount();
        int oldAvailableCount = book.getAvailableCount();
        BeanUtils.copyProperties(updatedBook, book);
        int newTotalCount = updatedBook.getTotalCount();
        if (newTotalCount > oldTotalCount) {
            int increaseBy = newTotalCount - oldTotalCount;
            book.setAvailableCount(oldAvailableCount + increaseBy);
        } else if (newTotalCount < oldTotalCount) {
            int decreaseBy = oldTotalCount - newTotalCount;
            book.setAvailableCount(Math.max(oldAvailableCount - decreaseBy, 0));
        }

        if (book.getAvailableCount() > newTotalCount) {
            book.setAvailableCount(newTotalCount);
        }

        if (book.getAvailableCount() > oldAvailableCount) {
            eventPublisher.publishEvent(new BookUpdatedEvent(book.getId(), book.getAvailableCount()));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null) book.setUpdatedBy(authentication.getName());
        book.setUpdatedDate(LocalDateTime.now());

        return bookRepository.save(book);
    }


    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book with ID " + id + " not found.");
        }
        bookRepository.deleteById(id);
    }

}
