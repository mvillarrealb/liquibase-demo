package org.mvillabe.books.domain.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mvillabe.books.domain.events.BookCreatedEvent;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity(name = "book")
@NoArgsConstructor
public class Book extends AbstractAggregateRoot<Book> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;
    @Column
    private String bookName;

    @Column(name = "book_isbn")
    private String bookISBN;

    @Column(name = "book_cover")
    private String coverURL;

    @Column(name = "book_year")
    private int bookYear;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    public Book(String bookName, String bookISBN, Author author) {
        Objects.requireNonNull(bookName, "bookName is required");
        Objects.requireNonNull(bookISBN, "isbn is required");
        Objects.requireNonNull(author, "Author is required");
        this.bookName = StringUtils.capitalize(bookName);
        this.bookISBN = bookISBN;
        this.author = author;
    }

    @PostPersist
    public void bookCreated() {
        this.registerEvent(new BookCreatedEvent(this));
    }
}
