package org.mvillabe.books.api.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class AuthorDTO {
    private Long authorId;
    @NotNull
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
