package org.mvillabe.books.domain.repositories;

import org.mvillabe.books.domain.entities.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = { "author" })
    Optional<Book> findById(Long id);

    @EntityGraph(attributePaths = { "author" })
    List<Book> findAll();
}
