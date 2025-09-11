package kr.co.hanipactor.application.manager.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StoreListReq {
    private String startDate;
    private String endDate;
    private String startOpenDate;
    private String endOpenDate;
    private String name;
    private String ownerName;
    private String businessNumber;
    private String category;
    private String address;
    private String tel;
    private int isActive;
    private int pageNumber;
    private int pageSize;
}
