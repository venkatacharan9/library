package com.library.management.Event;
import com.library.management.entity.BorrowRecord;
import lombok.Getter;

import org.springframework.context.ApplicationEvent;

@Getter
public class BookReturnedEvent extends ApplicationEvent {
    private final BorrowRecord borrowRecord;

    public BookReturnedEvent(Object source, BorrowRecord borrowRecord) {
        super(source);
        this.borrowRecord = borrowRecord;
    }
}
