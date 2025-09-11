package kr.co.hanipactor.application.manager.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class StoreListRes {
    private long storeId;
    private String openDate;
    private String name;
    private String ownerName;
    private String businessNumber;
    private List<String> categories;
    private String address;
    private String tel;
    private int isActive;
    private String createdAt;
}
