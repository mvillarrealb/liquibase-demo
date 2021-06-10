package org.mvillabe.books.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The requested file does not exist")
public class FileNotFoundException extends RuntimeException  {
}
