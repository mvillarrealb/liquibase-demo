package org.mvillabe.books.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvillabe.books.api.dtos.BookStoreDTO;
import org.mvillabe.books.api.dtos.StoreScheduleDTO;
import org.mvillabe.books.domain.entities.BookStore;
import org.mvillabe.books.domain.repositories.BookStoreRepository;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class BookStoreService {

    private final BookStoreRepository bookStoreRepository;
    private final ObjectMapper objectMapper;

    public BookStoreDTO createBookStore(BookStoreDTO bookStoreDTO) {
        BookStore bookStore = createDomain(bookStoreDTO);
        bookStore = bookStoreRepository.save(bookStore);
        return createDTO(bookStore);
    }

    public List<BookStoreDTO> findBookStores() {
        return bookStoreRepository.findAll().stream().map(this::createDTO).collect(Collectors.toList());
    }

    public BookStoreDTO findBookStore(Long bookStoreId) {
        Optional<BookStore> bookStoreOptional = bookStoreRepository.findById(bookStoreId);
        BookStore bookStore = bookStoreOptional.orElseThrow();
        return createDTO(bookStore);
    }

    private BookStoreDTO createDTO(BookStore bookStore) {
        List<StoreScheduleDTO> businessHours = new ArrayList<>();
        try {
            businessHours = objectMapper.readValue(bookStore.getStoreBusinessHours(), businessHours.getClass());
        } catch (JsonProcessingException e) {
            log.error("Error deserializing business hours from Json with message {}", e.getMessage(), e);
        }
        BookStoreDTO bookStoreDTO = new BookStoreDTO();
        bookStoreDTO.setStoreId(bookStore.getStoreId());
        bookStoreDTO.setStoreName(bookStore.getStoreName());
        bookStoreDTO.setStoreAddress(bookStore.getStoreAddress());
        bookStoreDTO.setStoreSchedule(businessHours);
        return bookStoreDTO;
    }

    private BookStore createDomain(BookStoreDTO bookStoreDTO) {
        String businessHours = null;
        try {
            businessHours = objectMapper.writeValueAsString(bookStoreDTO.getStoreSchedule());
        } catch (JsonProcessingException e) {
            log.error("Error serializing business hours to Json with message {}", e.getMessage(), e);
        }
        return new BookStore(bookStoreDTO.getStoreName(), bookStoreDTO.getStoreAddress(), businessHours);
    }
}
