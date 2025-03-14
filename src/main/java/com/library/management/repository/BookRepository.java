package com.library.management.repository;

import com.library.management.entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByTitleAndAuthor(String title, String author);

    @Query(value = "SELECT * FROM BOOKS b " +
            "WHERE (:title IS NULL OR LOWER(b.TITLE) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:author IS NULL OR LOWER(b.AUTHOR) LIKE LOWER(CONCAT('%', :author, '%')))",
            nativeQuery = true)
    Page<Book> searchBooks(@Param("title") String title,
                           @Param("author") String author,
                           Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdAndLock(@Param("id") Long id);

}

