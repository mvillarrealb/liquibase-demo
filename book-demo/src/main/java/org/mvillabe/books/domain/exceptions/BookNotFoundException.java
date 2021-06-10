package org.mvillabe.books.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The requested book does not exist")
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long bookId) {
        super("Book with id "+bookId + "Was not found");
    }
}
