package org.mvillabe.books.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class BookStockId implements Serializable {
    private Long bookId;
    private Long storeId;
}
