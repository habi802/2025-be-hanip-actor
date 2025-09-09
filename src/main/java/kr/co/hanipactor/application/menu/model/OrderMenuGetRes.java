package kr.co.hanipactor.application.menu.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class OrderMenuGetRes {
    private Long menuId;
    private String name;
    private List<Option> options;

    // 메뉴의 옵션
    @Getter
    @Builder
    @ToString
    public static class Option {
        private Long optionId;
        private String comment;
        // 옵션의 하위 옵션
        private List<Option> children;
    }
}
