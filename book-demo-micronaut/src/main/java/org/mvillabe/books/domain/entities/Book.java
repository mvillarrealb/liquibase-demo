package org.mvillabe.books.domain.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity(name = "book")
@NoArgsConstructor
public class Book extends BaseEntity {
    @Id
    private Long bookId;

    @Column
    private String authorName;

    @Column
    private String bookName;

    @Column
    private String bookISBN;

    public Book(Long bookId, String authorName, String bookName, String bookISBN) {
        this.bookId = bookId;
        this.authorName = authorName;
        this.bookName = bookName;
        this.bookISBN = bookISBN;
    }
}
