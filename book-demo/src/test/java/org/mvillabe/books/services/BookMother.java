package org.mvillabe.books.services;

import com.github.javafaker.Faker;
import org.mvillabe.books.api.dtos.BookDTO;
import org.mvillabe.books.domain.entities.Book;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class BookMother {
    public static BookDTO random() {
        Faker faker = new Faker();
        BookDTO bookDTO = new BookDTO();
        bookDTO.setAuthorId(faker.number().randomNumber());
        bookDTO.setBookId(faker.random().nextLong());
        bookDTO.setBookName(faker.book().title());
        bookDTO.setBookISBN(faker.random().toString());
        return bookDTO;
    }

    public static Book create(String bookName, String isbn) {
        Faker faker = new Faker();
        Book book = new Book(bookName, isbn, AuthorMother.randomAuthor());
        book.setBookId(faker.random().nextLong());
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        return book;
    }

    public static List<Book> authorList() {
        return Arrays.asList(randomBook(), randomBook(), randomBook());
    }

    public static Book randomBook() {
        Faker faker = new Faker();
        Book book = new Book(faker.book().title(), faker.random().toString(), AuthorMother.randomAuthor());
        book.setBookId(faker.random().nextLong());
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        return book;
    }
}
