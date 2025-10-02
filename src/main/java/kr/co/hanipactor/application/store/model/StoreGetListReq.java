package kr.co.hanipactor.application.store.model;

import lombok.Getter;
import lombok.ToString;

import java.beans.ConstructorProperties;

@Getter
@ToString
public class StoreGetListReq {
    private String category;
    private String searchText;

    private Integer page;
    private Integer size;

    @ConstructorProperties({"category", "searchText", "page", "size"})
    public StoreGetListReq(String category, String searchText, Integer page, Integer size) {
        this.category = category;
        this.searchText = searchText;
        this.page = page - 1;
        this.size = size;
    }
}
