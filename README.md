
# Library Management System

This is a Spring Boot-based **Library Management System** that allows users to borrow and return books, manage book queues, and handle late fees. The system is designed to handle concurrent access using optimistic locking and includes Flyway for database versioning and migration.

##  Setup & Installation
    git clone https://github.com/venkatacharan9/library.git
    mvn flyway:migrate (Run flyway migration)
    mvn clean install
    mvn spring-boot:run
    [ Download Postman Collection] https://github.com/venkatacharan9/library/blob/master/postman/LibraryManagementSystem.postman_collection.jso
    

##  Features
    Borrow and return books with queue handling  
    Late fee calculation based on flexible rules  
    Email notifications for queue updates  
    Flyway-based database migration  
    Search and filter books by title and author  


## Tech Stack
- **Java** – Version 17+
- **Spring Boot** – For application framework
- **Spring Data JPA** – For ORM and repository management
- **Flyway** – For database versioning and migration
- **H2/PostgreSQL** – For relational database
