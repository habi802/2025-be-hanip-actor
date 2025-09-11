package kr.co.hanipactor.application.manager.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StoreListReq {
    private String startDate;
    private String endDate;
    private int pageNumber;
    private int pageSize;
}
