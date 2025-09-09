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
    private String imagePath;

    public static class Option {
        private Long optionId;
        private String comment;
        private int price;
        private List<subOption> subOptions;

        public static class subOption {
            private Long optionId;
            private String comment;
            private int price;
        }
    }
}
