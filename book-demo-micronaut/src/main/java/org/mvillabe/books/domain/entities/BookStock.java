package org.mvillabe.books.domain.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@IdClass(BookStockId.class)
@Entity(name = "book_store_stock")
@NoArgsConstructor
public class BookStock extends BaseEntity {
    @Id
    @Column(name = "book_id")
    private Long bookId;

    @Id
    @Column(name = "store_id")
    private Long storeId;

    @Column
    private Long bookStock;

    @Column
    private BigDecimal bookPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    private BookStore store;

    public BookStock(Long bookId, Long storeId, Long bookStock, BigDecimal bookPrice) {
        this.bookId = bookId;
        this.storeId = storeId;
        this.bookStock = bookStock;
        this.bookPrice = bookPrice;
    }
}
