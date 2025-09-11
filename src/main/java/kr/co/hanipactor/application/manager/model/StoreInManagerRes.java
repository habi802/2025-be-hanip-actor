package kr.co.hanipactor.application.manager.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class StoreInManagerRes {
    private String name;
    private String imagePath;
    private List<String> categories;
    private String businessNumber;
    private String licensePath;
    private String ownerName;
    private String openDate;
    private String address;
    private int isActive;
    private String comment;
    private String eventComment;
    private String tel;
    private String openTime;
    private String closeTime;
    private String closedDay;
    private int isOpen;
    private int isPickUp;
    private int minDeliveryFee;
    private int maxDeliveryFee;
    private int minAmount;
    private double rating;
    private int favorites;
}
