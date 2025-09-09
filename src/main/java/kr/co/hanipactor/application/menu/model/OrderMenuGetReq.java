package kr.co.hanipactor.application.menu.model;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class OrderMenuGetReq {
    private List<Long> menuIds;
    private List<Long> optionIds;
}
