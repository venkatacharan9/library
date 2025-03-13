package com.library.management.service;

import com.library.management.dto.BookDto;
import com.library.management.entity.Book;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {

    public Page<Book> getAllBooks(int page, int size);
    public Book getBookById(Long id) ;
    public Page<Book> searchBooks(String title, String author,int page,int size);
    public Book addBook(BookDto bookDto);
    public Book updateBook(Long id, BookDto updatedBook);
    public void deleteBook(Long id);
    }
