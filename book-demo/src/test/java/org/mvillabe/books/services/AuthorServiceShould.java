package org.mvillabe.books.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mvillabe.books.api.dtos.AuthorDTO;
import org.mvillabe.books.api.services.AuthorService;
import org.mvillabe.books.domain.entities.Author;
import org.mvillabe.books.domain.exceptions.AuthorNotFoundException;
import org.mvillabe.books.domain.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorServiceShould {
    private AuthorService authorService;
    private AuthorRepository authorRepository;

    @BeforeEach
    public void beforeAll() {
        authorRepository = mock(AuthorRepository.class);
        authorService = new AuthorService(authorRepository);
    }

    @Test
    public void validateAuthor() {
        assertThrows(NullPointerException.class, () -> new Author(null));
    }

    @Test
    public void createAuthor() {
       AuthorDTO authorDTO = AuthorMother.random();
       Author author = AuthorMother.create(authorDTO.getAuthorName());
       when(authorRepository.save(any(Author.class))).thenReturn(author);
       AuthorDTO createdDTO = authorService.createAuthor(authorDTO);
       assertNotNull(createdDTO.getAuthorId());
       assertNotNull(createdDTO.getCreatedAt());
       assertNotNull(createdDTO.getUpdatedAt());
    }

    @Test
    public void listAuthors() {
        List<Author> authorList = AuthorMother.authorList();
        when(authorRepository.findAll()).thenReturn(authorList);
        List<AuthorDTO> authorDTOS = authorService.findAuthors();
        assertEquals(authorList.size(), authorDTOS.size());
    }

    @Test
    public void findAuthorById() {
        Author author = AuthorMother.randomAuthor();
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        AuthorDTO authorDTO = authorService.findAuthor(1L);
        assertNotNull(authorDTO);
        assertNotNull(authorDTO.getAuthorId());
        assertNotNull(authorDTO.getCreatedAt());
        assertNotNull(authorDTO.getUpdatedAt());
    }

    @Test
    public void handleErrors() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AuthorNotFoundException.class, () -> authorService.findAuthor(1L));
        assertThrows(AuthorNotFoundException.class, () -> authorService.findAuthorById(1L));
    }
}
