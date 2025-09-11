package kr.co.hanipactor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StoreCategory extends CreatedAt {
    @EmbeddedId
    private StoreCategoryIds storeCategoryId;

    @ManyToOne
    @MapsId("storeId")
    @JoinColumn(name = "store_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Store store;
}
