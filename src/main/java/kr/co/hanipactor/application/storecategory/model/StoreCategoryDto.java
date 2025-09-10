package kr.co.hanipactor.application.storecategory.model;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class StoreCategoryDto {
    private Long storeId;
    private String category;
}
