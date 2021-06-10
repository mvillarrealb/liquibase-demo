package org.mvillabe.books.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvillabe.books.api.dtos.BookStockDTO;
import org.mvillabe.books.api.dtos.CreateBookStockDTO;
import org.mvillabe.books.asyncapi.BookClient;
import org.mvillabe.books.domain.entities.Book;
import org.mvillabe.books.domain.entities.BookStock;
import org.mvillabe.books.domain.entities.BookStockId;
import org.mvillabe.books.domain.repositories.BookStockRepository;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class BookStockService {

    private final BookStockRepository bookStockRepository;

    private final BookClient bookClient;

    private final ObjectMapper objectMapper;

    public List<BookStockDTO> findBookStock(Long bookId) {
        return bookStockRepository.findAllByBookId(bookId)
                .stream()
                .map(this::createDTO)
                .collect(Collectors.toList());
    }

    public List<BookStockDTO> createBookStock(Long bookId, List<CreateBookStockDTO> bookStockDTOList) {
       List<BookStock> bookStockList = bookStockDTOList
                .stream()
                .map(dto -> new BookStock(bookId, dto.getStoreId(), dto.getStock(), dto.getPrice()))
               .collect(Collectors.toList());
       bookStockRepository.saveAll(bookStockList);
        List<Long> storeIds = bookStockDTOList.stream().map(CreateBookStockDTO::getStoreId).collect(Collectors.toList());
        return  bookStockRepository
                .findAllByBookIdAndStoreIdIn(bookId, storeIds)
                .stream()
                .map(this::createDTO)
                .collect(Collectors.toList());
    }

    public BookStockDTO updateBookStock(Long bookId, CreateBookStockDTO bookStockDTO) {
        BookStock bookStock = new BookStock(bookId, bookStockDTO.getStoreId(), bookStockDTO.getStock(), bookStockDTO.getPrice());
        bookStockRepository.save(bookStock);
        BookStock stock = bookStockRepository.findById(new BookStockId(bookId, bookStockDTO.getStoreId())).get();
        return createDTO(stock);
    }

    private BookStockDTO createDTO(BookStock bookStock) {
        return BookStockDTO.builder()
                .author(bookStock.getBook().getAuthorName())
                .bookName(bookStock.getBook().getBookName())
                .price(bookStock.getBookPrice())
                .stock(bookStock.getBookStock())
                .storeName(bookStock.getStore().getStoreName())
                .storeAddress(bookStock.getStore().getStoreAddress())
                .createdAt(bookStock.getCreatedAt())
                .updatedAt(bookStock.getUpdatedAt())
                .build();
    }

    public void createBook(Long bookId) {
        Book book = randomBook(bookId);
        try {
            bookClient.send(objectMapper.writeValueAsBytes(book));
        } catch (JsonProcessingException e) {
           log.error("Error serializing book data as bytes with message {} ", e.getMessage(), e);
        }
    }

    private Book randomBook(Long bookId) {
        Faker faker = new Faker();
        return new Book(bookId, faker.book().author(), faker.book().title(), faker.random().nextInt(10000,99999).toString());
    }
}
