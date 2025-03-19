package com.library.management.Event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookUpdatedEvent {
    private Long bookId;
    private int availableCount;
}
