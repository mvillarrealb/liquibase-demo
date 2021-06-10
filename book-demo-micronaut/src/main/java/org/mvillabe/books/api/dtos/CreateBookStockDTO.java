package org.mvillabe.books.api.dtos;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Introspected
@Data
public class CreateBookStockDTO {
    @NotNull
    private Long storeId;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Long stock;
}
