package kr.co.hanipactor.application.store.model;

import kr.co.hanipactor.configuration.enumcode.model.EnumDayOfWeek;
import kr.co.hanipactor.configuration.enumcode.model.EnumStoreCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class StorePutReq {
    private Long id;
    private String comment;
    private String tel;
    private List<EnumStoreCategory> storeCategory;

    private Integer isOpen;
    private String openTime;
    private String closeTime;
    private EnumDayOfWeek dayOfWeek;
    private Integer minDeliveryFee;
    private Integer maxDeliveryFee;
    private Integer minAmount;
    private Integer isPickUp;
    private String eventComment;

}
