package com.library.management;

import com.library.management.dto.BookDto;
import com.library.management.entity.Book;
import com.library.management.repository.BookRepository;
import com.library.management.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    public void setup() {
        bookRepository.deleteAll();
    }

    @Test
    public void testAddBook() {
        BookDto bookDto = new BookDto();
        bookDto.setTitle("Test Book");
        bookDto.setAuthor("Test Author");
        bookDto.setTotalCount(10);
        bookDto.setCreatedBy("Test");
        bookDto.setCreatedDate(LocalDateTime.now());
        Book savedBook = bookService.addBook(bookDto);

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Test Book");
        assertThat(savedBook.getAuthor()).isEqualTo("Test Author");
        assertThat(savedBook.getTotalCount()).isEqualTo(10);

    }

    @Test
    public void testGetBookById() {
        BookDto bookDto = new BookDto();
        bookDto.setTitle("Another Book");
        bookDto.setAuthor("Another Author");
        bookDto.setTotalCount(10);
        bookDto.setCreatedBy("Test2");
        bookDto.setCreatedDate(LocalDateTime.now());
        Book savedBook = bookService.addBook(bookDto);

        Book foundBook = bookService.getBookById(savedBook.getId());

        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getId()).isEqualTo(savedBook.getId());
    }

    @Test
    public void testUpdateBook() {
        BookDto bookDto = new BookDto();
        bookDto.setTitle("Update Book");
        bookDto.setAuthor("Update Author");
        bookDto.setTotalCount(10);
        bookDto.setCreatedBy("Test");
        bookDto.setCreatedDate(LocalDateTime.now());
        bookDto.setUpdatedBy("Test");
        bookDto.setUpdatedDate(LocalDateTime.now().plusDays(2));
        Book savedBook = bookService.addBook(bookDto);

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setTitle("Updated Book Title");
        updatedBookDto.setAuthor("Updated Author");
        updatedBookDto.setTotalCount(11);
        updatedBookDto.setCreatedBy("Test");
        updatedBookDto.setCreatedDate(LocalDateTime.now());
        bookDto.setUpdatedBy("Test");
        bookDto.setUpdatedDate(LocalDateTime.now().plusDays(2));

        Book updatedBook = bookService.updateBook(savedBook.getId(), updatedBookDto);

        assertThat(updatedBook.getTitle()).isEqualTo("Updated Book Title");
        assertThat(updatedBook.getAuthor()).isEqualTo("Updated Author");
        assertThat(updatedBook.getTotalCount()).isEqualTo(11);

    }

    @Test
    public void testDeleteBook() {
        BookDto bookDto = new BookDto();
        bookDto.setTitle("Delete Book");
        bookDto.setAuthor("Delete Author");
        bookDto.setTotalCount(10);
        bookDto.setCreatedBy("Test1");
        bookDto.setCreatedDate(LocalDateTime.now());
        Book savedBook = bookService.addBook(bookDto);

        bookService.deleteBook(savedBook.getId());

        assertThat(bookRepository.existsById(savedBook.getId())).isFalse();
    }
}
