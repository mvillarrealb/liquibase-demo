package org.mvillabe.books.api.controllers;

import lombok.RequiredArgsConstructor;
import org.mvillabe.books.api.dtos.BookDTO;
import org.mvillabe.books.api.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@RequestBody @Valid BookDTO bookDTO) {
        return bookService.createBook(bookDTO);
    }

    @GetMapping("books")
    public List<BookDTO> findBooks() {
        return bookService.findBooks();
    }

    @GetMapping("books/{bookId}")
    public BookDTO findBook(@PathVariable("bookId") Long bookId) {
        return bookService.findBook(bookId);
    }

    @PutMapping(value = "books/{bookId}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> uploadCover(@PathVariable("bookId") Long bookId, @RequestPart("files") Mono<FilePart> filePartMono) {
        return  filePartMono
                .flatMap(it -> this.bookService.upload(bookId, it))
                .then();
    }
}
