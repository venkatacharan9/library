package com.library.management.serviceImpl;
import com.library.management.dto.BookDto;
import com.library.management.entity.Book;
import com.library.management.exception.ResourceAlreadyExistsException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
import com.library.management.repository.UserRepository;
import com.library.management.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public Page<Book> getAllBooks(int page, int size) {
        return bookRepository.findAll(PageRequest.of(page, size));
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found."));
    }

    public Page<Book> searchBooks(String title, String author, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Book> books = bookRepository.searchBooks(title, author, pageable);
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
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, BookDto updatedBook) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found."));
        BeanUtils.copyProperties(updatedBook,book);
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book with ID " + id + " not found.");
        }
        bookRepository.deleteById(id);
    }

}
