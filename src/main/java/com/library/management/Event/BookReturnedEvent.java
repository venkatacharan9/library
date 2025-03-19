package com.library.management.Event;
import com.library.management.entity.BorrowRecord;
import lombok.Getter;

import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookReturnedEvent {
    private final BorrowRecord borrowRecord;
}
