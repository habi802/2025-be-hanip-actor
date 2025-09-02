package kr.co.hanipactor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import kr.co.hanipactor.configuration.enumcode.model.EnumStoreCategory;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Embeddable
@EqualsAndHashCode
public class StoreCategoryIds implements Serializable {
    private long storeId;

    @Column(columnDefinition = "VARCHAR(2)")
    private EnumStoreCategory category;
}
