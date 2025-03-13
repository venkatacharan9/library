package com.library.management.repository;

import com.library.management.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    Page<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String author, Pageable pageable);

    boolean existsByTitleAndAuthor(String title, String author);  // Check for duplicate books

    @Query(value = "SELECT * FROM BOOK b " +
            "WHERE (:title IS NULL OR LOWER(b.TITLE) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:author IS NULL OR LOWER(b.AUTHOR) LIKE LOWER(CONCAT('%', :author, '%')))",
            nativeQuery = true)
    Page<Book> searchBooks(@Param("title") String title,
                           @Param("author") String author,
                           Pageable pageable);

}

