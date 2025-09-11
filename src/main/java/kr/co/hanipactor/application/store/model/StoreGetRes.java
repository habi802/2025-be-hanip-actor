package kr.co.hanipactor.application.store.model;

import kr.co.hanipactor.entity.Store;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@ToString
public class StoreGetRes {
    private Long id;
    private int favorites;
    private Double rating;
    private int isOpen;
    private int isActive;
    private int isPickUp;
    private int maxDeliveryFee;
    private int minDeliveryFee;
    private int minAmount;
    private String businessNumber;
    private String ownerName;
    private String tel;
    private String name;
    private String postCode;
    private String address;
    private String addressDetail;
    private String imagePath;
    private String licensePath;
    private String comment;
    private String eventComment;
    private String openTIme;
    private String closeTime;
    private String openDate;

    public static StoreGetRes of(Store store) {
        return StoreGetRes.builder()
                .id(store.getId())
                .favorites(store.getFavorites())
                .rating(store.getRating())
                .isOpen(store.getIsOpen())
                .isActive(store.getIsActive())
                .isPickUp(store.getIsPickUp())
                .maxDeliveryFee(store.getMaxDeliveryFee())
                .minDeliveryFee(store.getMinDeliveryFee())
                .minAmount(store.getMinAmount())
                .businessNumber(store.getBusinessNumber())
                .ownerName(store.getOwnerName())
                .tel(store.getTel())
                .name(store.getName())
                .postCode(store.getPostcode())
                .address(store.getAddress())
                .addressDetail(store.getAddressDetail())
                .imagePath(store.getImagePath())
                .licensePath(store.getLicensePath())
                .comment(store.getComment())
                .eventComment(store.getEventComment())
                .openTIme(store.getOpenTime())
                .closeTime(store.getCloseTime())
                .openDate(store.getOpenDate())
                .build();
    }
}
