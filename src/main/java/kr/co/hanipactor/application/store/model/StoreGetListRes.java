package kr.co.hanipactor.application.store.model;

import kr.co.hanipactor.configuration.enumcode.model.EnumStoreCategory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class StoreGetListRes {
    private Long id;
    private String name;
    private String imagePath;
    private int isActive; // 활성화 여부
    private int isOpen; // 영업 여부
    private int maxDeliveryFee; // 최대 배달 요금
    private int minDeliveryFee; // 최소 배달 요금
    private int minAmount; // 최소 주문 금액
    private int rating; // 별점 평균
    private int favorites; // 좋아요 갯수
    private List<EnumStoreCategory> category; // 카테고리
}
