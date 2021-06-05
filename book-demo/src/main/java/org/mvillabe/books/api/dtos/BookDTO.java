package org.mvillabe.books.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDTO {

    private Long bookId;

    @NotNull
    private Long authorId;

    @NotNull
    private String bookName;

    @NotNull
    private String bookISBN;

    private String coverURL;

    private AuthorDTO author;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
