package com.library.management.service;

import com.library.management.dto.BookDto;
import com.library.management.entity.Book;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {

     Page<Book> getAllBooks(int page, int size);
     Book getBookById(Long id) ;
     Page<Book> searchBooks(String title, String author,String anyType,int page,int size);
     Book addBook(BookDto bookDto);
     Book updateBook(Long id, BookDto updatedBook);
     void deleteBook(Long id);
    }
