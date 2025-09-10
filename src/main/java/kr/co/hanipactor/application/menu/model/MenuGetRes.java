package kr.co.hanipactor.application.menu.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class MenuGetRes {
    private Long menuId;
    private String name;
    private int price;
    private String comment;
    private String imagePath;
    private List<Option> options;

    @Getter
    @Builder
    @ToString
    public static class Option {
        private Long optionId;
        private String comment;
        private int price;
        // 옵션의 하위 옵션
        private List<MenuGetRes.Option> children;
    }
}
