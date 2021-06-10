package org.mvillabe.books.domain.repositories;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import org.mvillabe.books.domain.entities.Book;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
}
