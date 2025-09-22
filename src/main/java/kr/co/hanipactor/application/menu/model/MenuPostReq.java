package kr.co.hanipactor.application.menu.model;

import kr.co.hanipactor.application.menuoption.model.MenuOptionPostReq;
import kr.co.hanipactor.configuration.enumcode.model.EnumMenuType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class MenuPostReq {
    private int id;
    private String name;
    private String comment;
    private int price;
    private String imagePath;
    private EnumMenuType menuType;
    private List<MenuOptionPostReq> menuOption;
}
