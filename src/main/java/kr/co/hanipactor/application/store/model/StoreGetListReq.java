package kr.co.hanipactor.application.store.model;

import lombok.Getter;
import lombok.ToString;

import java.beans.ConstructorProperties;

@Getter
@ToString
public class StoreGetListReq {
    private String category;
    private String searchText;
    private String sortColumn;
    private String sortOrder;

    private Integer page;
    private Integer size;

    @ConstructorProperties({"category", "searchText", "sortColumn", "sortOrder", "page", "size"})
    public StoreGetListReq(String category, String searchText, String sortColumn, String sortOrder, Integer page, Integer size) {
        this.category = category;
        this.searchText = searchText;
        this.sortColumn = sortColumn;
        this.sortOrder = sortOrder;
        this.page = page - 1;
        this.size = size;
    }
}
