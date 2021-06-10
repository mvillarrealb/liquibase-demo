package org.mvillabe.books.domain.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@Entity(name = "book_store")
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
@NoArgsConstructor
public class BookStore extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    @Column
    private String storeName;

    @Column
    private String storeAddress;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private String storeBusinessHours;

    public BookStore(String storeName, String storeAddress, String storeBusinessHours) {
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeBusinessHours = storeBusinessHours;
    }
}
