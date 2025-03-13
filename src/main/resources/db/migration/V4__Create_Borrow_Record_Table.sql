CREATE TABLE borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    borrowed_at TIMESTAMP NOT NULL,
    returned_at TIMESTAMP NULL,
    late_fee DOUBLE DEFAULT 0.0,
    CONSTRAINT fk_borrow_record_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_borrow_record_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
