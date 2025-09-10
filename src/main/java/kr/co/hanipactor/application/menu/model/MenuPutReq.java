package kr.co.hanipactor.application.menu.model;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class MenuPutReq {
    private Long menuId;
    private String name;
    private int price;
    private String comment;
    private String menuType;
    private List<Option> options;

    @Getter
    @ToString
    public static class Option {
        private Long optionId;
        private String comment;
        private Integer isRequired;
        private List<SubOption> subOptions;

        @Getter
        @ToString
        public static class SubOption {
            private Long optionId;
            private String comment;
            private int price;
        }
    }
}
