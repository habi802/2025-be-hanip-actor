package kr.co.hanipactor.application.store.model;

import kr.co.hanipactor.configuration.enumcode.model.EnumStoreCategory;
import lombok.Getter;
import lombok.ToString;

import java.beans.ConstructorProperties;

@Getter
@ToString
public class StoreGetListReq {
    private EnumStoreCategory category;
    private String searchText;

    private Integer page = 0;
    private Integer size = 20;

    @ConstructorProperties({"category", "search_text", "page", "size"})
    public StoreGetListReq(EnumStoreCategory category, String searchText, Integer page, Integer size) {
        this.category = category;
        this.searchText = searchText;
        this.page = page;
        this.size = size;
    }

    public int getOffset() {
        return page * size;
    }
}
