package org.mvillabe.books.domain.events;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.mvillabe.books.api.common.CommonEvent;
import org.mvillabe.books.domain.entities.Book;

@Data
@RequiredArgsConstructor
public class BookCreatedEvent extends CommonEvent {
    private final Book book;

    @Override
    public String getTopicId() {
        return "books.created";
    }
}
