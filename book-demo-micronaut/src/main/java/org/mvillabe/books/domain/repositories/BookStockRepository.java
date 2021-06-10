package org.mvillabe.books.domain.repositories;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.annotation.EntityGraph;
import io.micronaut.data.repository.CrudRepository;
import org.mvillabe.books.domain.entities.BookStock;
import org.mvillabe.books.domain.entities.BookStockId;

import java.util.List;
import java.util.Optional;


@Repository
public interface BookStockRepository extends CrudRepository<BookStock, BookStockId> {

    @EntityGraph(attributePaths = {"store", "book"})
    Optional<BookStock> findById(BookStockId bookStockId);

    @Join(value = "store",type = Join.Type.LEFT_FETCH)
    @Join(value = "book",type = Join.Type.LEFT_FETCH)
    List<BookStock> findAllByBookId(Long bookId);

    @Join(value = "store",type = Join.Type.LEFT_FETCH)
    @Join(value = "book",type = Join.Type.LEFT_FETCH)
    List<BookStock> findAllByBookIdAndStoreIdIn(Long bookId, List<Long> storeIds);
}
