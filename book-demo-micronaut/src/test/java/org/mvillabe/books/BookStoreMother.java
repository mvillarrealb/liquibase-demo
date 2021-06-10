package org.mvillabe.books;

import com.github.javafaker.Faker;
import org.mvillabe.books.domain.entities.BookStore;

public class BookStoreMother {
    public static BookStore random() {
        Faker faker = new Faker();
        return new BookStore(faker.commerce().department(), faker.address().fullAddress(), "");
    }
}
