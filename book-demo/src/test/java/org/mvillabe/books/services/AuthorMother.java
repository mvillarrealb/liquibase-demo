package org.mvillabe.books.services;

import com.github.javafaker.Faker;
import org.mvillabe.books.api.dtos.AuthorDTO;
import org.mvillabe.books.domain.entities.Author;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class AuthorMother {
    public static Author randomAuthor() {
        return create(random().getAuthorName());
    }
    public static AuthorDTO random() {
        Faker faker = new Faker();
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setAuthorName(faker.name().fullName());
        return authorDTO;
    }

    public static Author create(String authorName) {
        Faker faker = new Faker();
        Author author = new Author(authorName);
        author.setAuthorId(faker.number().randomNumber());
        author.setCreatedAt(LocalDateTime.now());
        author.setUpdatedAt(LocalDateTime.now());
        return author;
    }

    public static List<Author> authorList() {
        return Arrays.asList(create(random().getAuthorName()), create(random().getAuthorName()), create(random().getAuthorName()));
    }
}
