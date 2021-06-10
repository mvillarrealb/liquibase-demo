package org.mvillabe.books.api.services;

import com.google.cloud.storage.BlobId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvillabe.books.api.dtos.AuthorDTO;
import org.mvillabe.books.api.dtos.BookDTO;
import org.mvillabe.books.domain.entities.Author;
import org.mvillabe.books.domain.entities.Book;

import org.mvillabe.books.domain.exceptions.BookNotFoundException;
import org.mvillabe.books.domain.repositories.BookRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private final AuthorService authorService;

    private final StorageService storageService;


    public BookDTO createBook(BookDTO bookDTO) {
        Book book = bookRepository.save(createEntity(bookDTO));
        return createDTO(book);
    }

    public List<BookDTO> findBooks() {
        return bookRepository.findAll().stream().map(this::createDTO).collect(Collectors.toList());
    }

    public BookDTO findBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        return createDTO(book);
    }

    private BookDTO createDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        AuthorDTO authorDTO = authorService.createDTO(book.getAuthor());
        BeanUtils.copyProperties(book, bookDTO);
        bookDTO.setAuthor(authorDTO);
        return bookDTO;
    }

    private Book createEntity(BookDTO bookDTO) {
        /*This already throws 404 :)*/
        Author author = authorService.findAuthorById(bookDTO.getAuthorId());
        return new Book(bookDTO.getBookName(), bookDTO.getBookISBN(), author);
    }
    /**
     *
     * @param bookId
     * @param filePart
     * @return
     */
    public Mono<Void> upload(Long bookId, FilePart filePart) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        BlobId blobId = BlobId.of("attachments", "book-"+bookId+".jpg");
        storageService.uploadFile(blobId, filePart, (url) -> {
            log.info("Attaching url for book cover {}", url);
            book.setCoverURL(url);
            bookRepository.save(book);
            log.info("Successfully attached book cover url for book {} with url {}", bookId, url);
        });
        return Mono.empty().then();
    }

    public byte[] getBookCover(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
        BlobId blobId = BlobId.of("attachments", "book-"+bookId+".jpg");
        return storageService.getFileBytes(blobId);
    }
}
