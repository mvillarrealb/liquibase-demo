package org.mvillabe.books.api.controllers;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import lombok.RequiredArgsConstructor;
import org.mvillabe.books.api.dtos.BookStoreDTO;
import org.mvillabe.books.api.services.BookStoreService;

import javax.validation.Valid;
import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
public class BookStoreController {

    private final BookStoreService bookStoreService;

    @Post("bookstores")
    public BookStoreDTO createBookStore(@Body @Valid BookStoreDTO bookStoreDTO) {
        return bookStoreService.createBookStore(bookStoreDTO);
    }
    @Get("bookstores")
    public List<BookStoreDTO> findBookStores() {
        return bookStoreService.findBookStores();
    }

    @Get("bookstores/{bookStoreId}")
    public BookStoreDTO findBookStore(Long bookStoreId) {
        return bookStoreService.findBookStore(bookStoreId);
    }
}
