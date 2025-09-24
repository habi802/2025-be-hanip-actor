package kr.co.hanipactor.application.menu.model;

import kr.co.hanipactor.configuration.enumcode.model.EnumMenuType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class MenuGetRes {
    private Long storeId;
    private Long menuId;
    private String name;
    private int price;
    private String comment;
    private String imagePath;
    private int isHide;
    private int isSoldOut;
    private EnumMenuType menuType;
    private List<Option> options;

    @Getter
    @Builder
    @ToString
    public static class Option {
        private Long optionId;
        private String comment;
        private int price;
        private int isRequired;
        // 옵션의 하위 옵션
        private List<MenuGetRes.Option> children;
    }
}
