package kr.co.hanipactor.application.store.model;

import kr.co.hanipactor.configuration.enumcode.model.EnumStoreCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StoreJoinReq {
    private int id;
    private String name;
    private String comment;
    private String businessNumber;
    private String licensePath;
    private String imagePath;
    private String postcode;
    private String address;
    private String addressDetail;
    private String tel;
    private String ownerName;
    private String openDate;
    private List<EnumStoreCategory> enumStoreCategory;
}
