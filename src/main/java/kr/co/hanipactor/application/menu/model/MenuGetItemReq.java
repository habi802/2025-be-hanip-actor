package kr.co.hanipactor.application.menu.model;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

// 주문 내역 메뉴 조회 Request용 객체
@Getter
@ToString
public class MenuGetItemReq {
    private List<Long> menuId;
    private List<Long> optionId;
}
