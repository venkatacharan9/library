CREATE TABLE book_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    notified boolean NOT NULL default false,
    queued_at TIMESTAMP NOT NULL,
    reserved_until TIMESTAMP NOT NULL,
    CONSTRAINT unique_book_queue UNIQUE (book_id, user_id),
    CONSTRAINT fk_book_queue_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_book_queue_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
