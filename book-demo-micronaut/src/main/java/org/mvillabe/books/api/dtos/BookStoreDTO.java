package org.mvillabe.books.api.dtos;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.validation.Validated;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Introspected
@Validated
public class BookStoreDTO {
    private Long storeId;

    @NotNull
    private String storeName;

    @NotNull
    private String storeAddress;

    @NotNull
    @Valid
    private List<StoreScheduleDTO> storeSchedule;
}
