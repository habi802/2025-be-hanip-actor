package kr.co.hanipactor.application.menu.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MenuPatchReq {
    private Long menuId;
    private Integer isSoldOut;
    private Integer isHide;
}
