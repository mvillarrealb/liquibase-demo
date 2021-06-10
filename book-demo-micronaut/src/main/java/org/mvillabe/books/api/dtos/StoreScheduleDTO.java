package org.mvillabe.books.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.core.annotation.Introspected;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Data
@Introspected
public class StoreScheduleDTO {
    @NotNull
    private List<DayOfWeek> dayOfWeekList;

    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime start;

    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime end;
}
