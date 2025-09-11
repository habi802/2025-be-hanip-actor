package kr.co.hanipactor.application.menu.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class OrderMenuGetRes {
    private Long menuId;
    private Long storeId;
    private String name;
    private int price;
    private String imagePath;
    private List<Option> options;

    // 메뉴의 옵션
    @Getter
    @Builder
    @ToString
    public static class Option {
        private Long optionId;
        private String comment;
        private int price;
        // 옵션의 하위 옵션
        private List<Option> children;
    }
}
