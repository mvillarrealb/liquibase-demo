package org.mvillabe.books.asyncapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.gcp.pubsub.annotation.PubSubListener;
import io.micronaut.gcp.pubsub.annotation.Subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvillabe.books.domain.entities.Book;
import org.mvillabe.books.domain.repositories.BookRepository;

import javax.validation.ConstraintViolationException;
import java.io.IOException;


@PubSubListener
@RequiredArgsConstructor
@Slf4j
public class BookListener {

    private final BookRepository bookRepository;

    private final ObjectMapper objectMapper;

    @Subscription("BookListener.onBookCreated")
    public void onBookCreated(byte[] bookData) {
        log.info("Received a new book creation data {}", bookData);
        Book book = null;
        try {
            book = objectMapper.readValue(bookData, Book.class);
            log.info("Persisting bookData");
            bookRepository.save(book);
            log.info("Successfully persisted bookData");
        } catch (IOException e) {
            log.error("Error deserializing byte array with error {}", e.getMessage(), e);
        } catch(ConstraintViolationException violationException) {
            log.error("Violation Exception handling book storage with message {}", violationException.getMessage(), violationException);
        }
    }
}
