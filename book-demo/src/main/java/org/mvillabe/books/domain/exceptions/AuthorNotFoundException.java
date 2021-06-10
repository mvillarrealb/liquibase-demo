package org.mvillabe.books.domain.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The requested author does not exist")
public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(Long authorId) {
        super("Author with id "+authorId + "Was not found");
    }
}
