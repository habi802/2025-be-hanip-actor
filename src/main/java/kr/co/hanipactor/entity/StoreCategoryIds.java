package kr.co.hanipactor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import kr.co.hanipactor.configuration.enumcode.model.EnumStoreCategory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class StoreCategoryIds implements Serializable {
    @Comment("가게 아이디(stores)")
    private long storeId;

    @Column(columnDefinition = "VARCHAR(2)")
    @Comment("카테고리(01: 한식, 02: 중식, 03: 일식, 04: 양식, 05: 디저트, 06: 분식, 07: 패스트푸드, 08: 아시안, 09: 치킨, 10: 피자, 11: 야식")
    private EnumStoreCategory category;
}
