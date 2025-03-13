CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    total_count INT NOT NULL,
    available_count INT NOT NULL,
    CONSTRAINT unique_book UNIQUE (title, author)
);
