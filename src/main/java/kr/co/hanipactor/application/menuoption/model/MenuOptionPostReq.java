package kr.co.hanipactor.application.menuoption.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class MenuOptionPostReq {
    private String comment;
    private int price;
    private Integer isRequired;
    private Long parentId;
    private List<MenuOptionPostReq> children;
}
