package org.mvillabe.books.api.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvillabe.books.api.dtos.AuthorDTO;
import org.mvillabe.books.domain.entities.Author;
import org.mvillabe.books.domain.exceptions.AuthorNotFoundException;
import org.mvillabe.books.domain.repositories.AuthorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        log.info("Creating author with dto {}", authorDTO);
        Author author = authorRepository.save(createEntity(authorDTO));
        log.info("Successfully persisted author with response {}", author);
        return createDTO(author);
    }

    public List<AuthorDTO> findAuthors() {
        return authorRepository.findAll().stream().map(this::createDTO).collect(Collectors.toList());
    }

    public AuthorDTO findAuthor(Long authorId) {
        return createDTO(findAuthorById(authorId));
    }

    public Author findAuthorById(Long authorId) {
        log.info("Finding author with id {}", authorId);
        return authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));
    }


    public AuthorDTO createDTO(Author author) {
        AuthorDTO authorDTO = new AuthorDTO();
        BeanUtils.copyProperties(author, authorDTO);
        return authorDTO;
    }

    private Author createEntity(AuthorDTO authorDTO) {
        return new Author(authorDTO.getAuthorName());
    }
}
