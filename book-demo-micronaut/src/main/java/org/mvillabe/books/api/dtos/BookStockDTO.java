package org.mvillabe.books.api.dtos;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Introspected
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookStockDTO {
    private String bookName;
    private String author;
    private String storeName;
    private String storeAddress;
    private Map<String, Object> schedule;
    private BigDecimal price;
    private Long stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
