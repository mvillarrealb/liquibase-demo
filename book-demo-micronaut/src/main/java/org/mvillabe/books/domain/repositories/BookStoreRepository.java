package org.mvillabe.books.domain.repositories;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import org.mvillabe.books.domain.entities.BookStock;
import org.mvillabe.books.domain.entities.BookStore;

import java.util.List;

@Repository
public interface BookStoreRepository extends CrudRepository<BookStore, Long> {
    List<BookStore> findAll();
}
