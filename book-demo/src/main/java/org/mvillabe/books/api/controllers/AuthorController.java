package org.mvillabe.books.api.controllers;

import lombok.RequiredArgsConstructor;
import org.mvillabe.books.api.dtos.AuthorDTO;
import org.mvillabe.books.api.services.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping("authors")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDTO createAuthor(@RequestBody @Valid AuthorDTO authorDTO) {
        return authorService.createAuthor(authorDTO);
    }

    @GetMapping("authors")
    public List<AuthorDTO> findAuthors() {
        return authorService.findAuthors();
    }

    @GetMapping("authors/{authorId}")
    public AuthorDTO findAuthor(@PathVariable("authorId") Long authorId) {
        return authorService.findAuthor(authorId);
    }
}
