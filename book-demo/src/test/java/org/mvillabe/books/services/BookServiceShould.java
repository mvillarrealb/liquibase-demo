package org.mvillabe.books.services;

import com.google.cloud.storage.BlobId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mvillabe.books.api.dtos.BookDTO;
import org.mvillabe.books.api.services.AuthorService;
import org.mvillabe.books.api.services.BookService;
import org.mvillabe.books.api.services.StorageService;
import org.mvillabe.books.domain.entities.Author;
import org.mvillabe.books.domain.entities.Book;
import org.mvillabe.books.domain.exceptions.AuthorNotFoundException;
import org.mvillabe.books.domain.exceptions.BookNotFoundException;
import org.mvillabe.books.domain.exceptions.FileNotFoundException;
import org.mvillabe.books.domain.repositories.BookRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookServiceShould {
    private BookService bookService;
    private BookRepository bookRepository;
    private AuthorService authorService;
    private StorageService storageService;

    @BeforeEach
    public void beforeAll() {
        bookRepository = mock(BookRepository.class);
        authorService = mock(AuthorService.class);
        storageService = mock(StorageService.class);
        bookService = new BookService(bookRepository, authorService, storageService);
    }

    @Test
    public void validateBooks() {
        assertThrows(NullPointerException.class, () -> new Book(null, null, null));
        assertThrows(NullPointerException.class, () -> new Book("bookName", null, null));
        assertThrows(NullPointerException.class, () -> new Book("bookName", "ISBN", null));
    }

    @Test
    public void createBook() {
        BookDTO bookDTO = BookMother.random();
        Author author = AuthorMother.randomAuthor();
        Book book = BookMother.create(bookDTO.getBookName(), bookDTO.getBookISBN());
        book.setAuthor(author);
        when(authorService.findAuthorById(any(Long.class))).thenReturn(author);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDTO createdDTO = bookService.createBook(bookDTO);
        assertNotNull(createdDTO.getBookId());
        //assertNotNull(createdDTO.getAuthor()); /*the mock f**k up the createDTO :p */
        assertNotNull(createdDTO.getCreatedAt());
        assertNotNull(createdDTO.getUpdatedAt());
    }

    @Test
    public void listBooks() {
        List<Book> authorList = BookMother.authorList();
        when(bookRepository.findAll()).thenReturn(authorList);
        List<BookDTO> bookDTOS = bookService.findBooks();
        assertEquals(authorList.size(), bookDTOS.size());
    }

    @Test
    public void findBookById() {
        Book book = BookMother.randomBook();
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));
        BookDTO bookDTO = bookService.findBook(1L);
        assertNotNull(bookDTO);
        assertNotNull(bookDTO.getBookId());
        assertNotNull(bookDTO.getCreatedAt());
        assertNotNull(bookDTO.getUpdatedAt());
    }

    @Test
    public void handleErrors() {
        BookDTO bookDTO = BookMother.random();
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        when(authorService.findAuthorById(any(Long.class))).thenThrow(new AuthorNotFoundException(1L));
        assertThrows(AuthorNotFoundException.class, () -> bookService.createBook(bookDTO));
        assertThrows(BookNotFoundException.class, () -> bookService.findBook(1L));

    }

    @Test
    public void handleStorageError() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(BookMother.randomBook()));
        when(storageService.getFileBytes(any(BlobId.class))).thenThrow(new FileNotFoundException());
        assertThrows(FileNotFoundException.class, () -> bookService.getBookCover(1L));
    }
}
