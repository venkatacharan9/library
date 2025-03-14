package com.library.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity()
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "author"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private Integer totalCount;
    private Integer availableCount;
    @Version
    private Integer version;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        if (this.availableCount == null) {
            this.availableCount = this.totalCount;
        }
    }
}
