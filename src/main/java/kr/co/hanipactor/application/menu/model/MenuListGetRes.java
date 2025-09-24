package kr.co.hanipactor.application.menu.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class MenuListGetRes {
    private String menuType;
    private List<Menu> menus;

    @Getter
    @Builder
    @ToString
    public static class Menu {
        private Long storeId;
        private Long menuId;
        private String name;
        private int price;
        private int isHide;
        private int isSoldOut;
        private String comment;
        private String imagePath;
    }
}
