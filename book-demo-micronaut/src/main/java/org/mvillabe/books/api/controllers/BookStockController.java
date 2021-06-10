package org.mvillabe.books.api.controllers;

import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import lombok.RequiredArgsConstructor;
import org.mvillabe.books.api.dtos.BookStockDTO;
import org.mvillabe.books.api.dtos.CreateBookStockDTO;
import org.mvillabe.books.api.services.BookStockService;

import javax.validation.Valid;
import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
public class BookStockController {
    private final BookStockService bookStockService;

    /**+
     * Test endpoint
     */
    @Post("books/{bookId}/test")
    public void createBook(@PathVariable("bookId") Long bookId) {
        bookStockService.createBook(bookId);
    }

    @Get("books/{bookId}/stock")
    public List<BookStockDTO> findBookStock(@PathVariable("bookId")Long bookId) {
        return bookStockService.findBookStock(bookId);
    }

    @Post("books/{bookId}/stock")
    public List<BookStockDTO> createBookStock(@PathVariable("bookId")Long bookId, @Body @Valid List<CreateBookStockDTO> bookStockDTOList) {
        return bookStockService.createBookStock(bookId, bookStockDTOList);
    }

    @Put("books/{bookId}/stock")
    public BookStockDTO updateBookStock(@PathVariable("bookId") Long bookId, @Body @Valid CreateBookStockDTO bookStockDTO) {
        return bookStockService.updateBookStock(bookId, bookStockDTO);
    }


}
